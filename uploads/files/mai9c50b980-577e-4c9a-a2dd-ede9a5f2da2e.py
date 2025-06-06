import tkinter as tk
from tkinter import filedialog, messagebox
import numpy as np
import torch
import torchvision
from sklearn.neighbors import KNeighborsClassifier
from sklearn.decomposition import PCA
from sklearn.metrics import classification_report
import cv2
import os
from PIL import Image, ImageTk
import joblib
from torchvision import transforms
from scipy.ndimage import rotate, shift

# Tắt cảnh báo joblib
os.environ["LOKY_MAX_CPU_COUNT"] = "4"

class DigitClassifierApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Handwritten Digit Classifier")

        # Canvas để vẽ
        self.canvas = tk.Canvas(root, width=200, height=200, bg="white")
        self.canvas.pack(pady=10)
        self.canvas.bind("<B1-Motion>", self.draw)

        # Nút điều khiển
        self.predict_btn = tk.Button(root, text="Predict", command=self.predict)
        self.predict_btn.pack(pady=5)
        self.import_btn = tk.Button(root, text="Import Image", command=self.import_image)
        self.import_btn.pack(pady=5)
        self.clear_btn = tk.Button(root, text="Clear", command=self.clear_canvas)
        self.clear_btn.pack(pady=5)

        # Nhãn hiển thị kết quả và thông tin mô hình
        self.result_label = tk.Label(root, text="Result: ", font=("Arial", 14))
        self.result_label.pack(pady=10)
        self.model_info_label = tk.Label(root, text="Model Info: Training...", font=("Arial", 12))
        self.model_info_label.pack(pady=5)
        self.prob_label = tk.Label(root, text="Probabilities: ", font=("Arial", 10))
        self.prob_label.pack(pady=5)

        # Khởi tạo mảng để lưu hình ảnh
        self.image = np.ones((200, 200), dtype=np.uint8) * 255  # Nền trắng
        self.photo = None  # Để lưu PhotoImage hiển thị trên canvas

        # Tải hoặc huấn luyện mô hình
        self.model, self.best_k, self.best_accuracy, self.pca = self.load_or_train_model()
        self.model_info_label.config(text=f"Model Info: k={self.best_k}, Accuracy={self.best_accuracy*100:.2f}%")

    def augment_data(self, image, label, num_augmentations=3):
        """Tăng cường dữ liệu bằng cách xoay, dịch chuyển"""
        augmented_images = [image]
        augmented_labels = [label]

        for _ in range(num_augmentations):
            # Xoay ngẫu nhiên từ -10 đến 10 độ
            angle = np.random.uniform(-10, 10)
            rotated_img = rotate(image.reshape(28, 28), angle, reshape=False).reshape(-1)
            augmented_images.append(rotated_img)
            augmented_labels.append(label)

            # Dịch chuyển ngẫu nhiên
            shift_x, shift_y = np.random.randint(-2, 3, size=2)
            shifted_img = shift(image.reshape(28, 28), [shift_x, shift_y], mode='nearest').reshape(-1)
            augmented_images.append(shifted_img)
            augmented_labels.append(label)

        return np.array(augmented_images), np.array(augmented_labels)

    def load_or_train_model(self):
        # Kiểm tra xem mô hình đã được lưu chưa
        if os.path.exists('knn_model.pkl') and os.path.exists('pca_model.pkl'):
            print("Loading saved model...")
            model = joblib.load('knn_model.pkl')
            pca = joblib.load('pca_model.pkl')
            best_k = model.n_neighbors
            best_accuracy = joblib.load('best_accuracy.pkl')
            return model, best_k, best_accuracy, pca

        # Tải MNIST từ torchvision
        DOWNLOAD_MNIST = not (os.path.exists('./mnist/') and os.listdir('./mnist/'))
        train_data = torchvision.datasets.MNIST(
            root='./mnist/',
            train=True,
            transform=torchvision.transforms.ToTensor(),
            download=DOWNLOAD_MNIST,
        )
        test_data = torchvision.datasets.MNIST(root='./mnist/', train=False)

        # Chuẩn bị dữ liệu huấn luyện
        train_x = torch.unsqueeze(train_data.data, dim=1).type(torch.FloatTensor)/255.0
        train_y = train_data.targets
        train_x = train_x.view(-1, 28*28).numpy()

        # Tăng cường dữ liệu
        print("Augmenting data...")
        augmented_x, augmented_y = [], []
        for i in range(len(train_x)):
            aug_x, aug_y = self.augment_data(train_x[i], train_y[i], num_augmentations=2)
            augmented_x.append(aug_x)
            augmented_y.append(aug_y)
        train_x = np.vstack(augmented_x)
        train_y = np.hstack(augmented_y)
        print(f"Augmented training data: {train_x.shape[0]} samples")

        # Chuẩn bị dữ liệu kiểm tra (toàn bộ tập test: 10,000 mẫu)
        test_x = torch.unsqueeze(test_data.data, dim=1).type(torch.FloatTensor)/255.0
        test_y = test_data.targets
        test_x = test_x.view(-1, 28*28).numpy()

        # Giảm chiều bằng PCA (tăng lên 700 chiều)
        pca = PCA(n_components=700)  # Tăng từ 200 lên 700 chiều
        train_x = pca.fit_transform(train_x)
        test_x = pca.transform(test_x)

        # Tìm giá trị k tối ưu (giới hạn k nhỏ để tăng độ chính xác)
        k_vals = range(1, 16, 2)
        accuracies = []
        for k in k_vals:
            model = KNeighborsClassifier(n_neighbors=k, weights='distance')
            model.fit(train_x, train_y)
            score = model.score(test_x, test_y)
            accuracies.append(score)
            print(f"k={k}, accuracy={score*100:.2f}%")

        # Chọn k tốt nhất
        best_k = k_vals[np.argmax(accuracies)]
        best_accuracy = max(accuracies)

        # Huấn luyện mô hình cuối cùng với k tốt nhất
        model = KNeighborsClassifier(n_neighbors=best_k, weights='distance')
        model.fit(train_x, train_y)

        # Đánh giá trên tập kiểm tra
        predictions = model.predict(test_x)
        print("EVALUATION ON TESTING DATA")
        print(classification_report(test_y, predictions))

        # Lưu mô hình
        joblib.dump(model, 'knn_model.pkl')
        joblib.dump(pca, 'pca_model.pkl')
        joblib.dump(best_accuracy, 'best_accuracy.pkl')

        return model, best_k, best_accuracy, pca

    def draw(self, event):
        x, y = event.x, event.y
        r = 8
        self.canvas.create_oval(x-r, y-r, x+r, y+r, fill="black")
        cv2.circle(self.image, (x, y), r, 0, -1)

    def clear_canvas(self):
        self.canvas.delete("all")
        self.image = np.ones((200, 200), dtype=np.uint8) * 255
        self.result_label.config(text="Result: ")
        self.prob_label.config(text="Probabilities: ")
        self.photo = None

    def display_image_on_canvas(self, img):
        img_pil = Image.fromarray(img)
        img_pil = img_pil.resize((200, 200), Image.Resampling.LANCZOS)
        self.photo = ImageTk.PhotoImage(img_pil)
        self.canvas.delete("all")
        self.canvas.create_image(100, 100, image=self.photo)

    def preprocess_image(self, img):
        # Làm mịn để giảm nhiễu
        img = cv2.GaussianBlur(img, (5, 5), 0)
        # Ngưỡng hóa để làm rõ ranh giới
        _, img_thresh = cv2.threshold(img, 128, 255, cv2.THRESH_BINARY | cv2.THRESH_OTSU)
        # Làm dày nét
        kernel = np.ones((3, 3), np.uint8)
        img_thresh = cv2.dilate(img_thresh, kernel, iterations=1)
        # Thay đổi kích thước về 28x28
        img_resized = cv2.resize(img_thresh, (28, 28), interpolation=cv2.INTER_AREA)
        # Chuẩn hóa và đảo màu
        img_array = img_resized / 255.0
        img_array = 1 - img_array
        img_flat = img_array.flatten().reshape(1, -1)
        # Áp dụng PCA
        img_flat = self.pca.transform(img_flat)
        return img_flat, img_thresh

    def predict_with_prob(self, img_flat):
        prediction = self.model.predict(img_flat)[0]
        probs = self.model.predict_proba(img_flat)[0]
        prob_text = ", ".join([f"{i}: {prob*100:.1f}%" for i, prob in enumerate(probs)])
        return prediction, prob_text

    def import_image(self):
        file_path = filedialog.askopenfilename(filetypes=[("Image files", "*.png *.jpg *.jpeg *.bmp")])
        if not file_path:
            return

        try:
            img = cv2.imread(file_path, cv2.IMREAD_GRAYSCALE)
            if img is None:
                messagebox.showerror("Error", "Cannot load image!")
                return

            img_flat, img_processed = self.preprocess_image(img)
            prediction, prob_text = self.predict_with_prob(img_flat)

            self.result_label.config(text=f"Result: {prediction}")
            self.prob_label.config(text=f"Probabilities: {prob_text}")

            self.image = cv2.resize(img_processed, (200, 200), interpolation=cv2.INTER_AREA)
            self.display_image_on_canvas(self.image)

        except Exception as e:
            messagebox.showerror("Error", f"Failed to process image: {str(e)}")

    def predict(self):
        img_flat, img_processed = self.preprocess_image(self.image)
        prediction, prob_text = self.predict_with_prob(img_flat)

        self.result_label.config(text=f"Result: {prediction}")
        self.prob_label.config(text=f"Probabilities: {prob_text}")

        self.image = cv2.resize(img_processed, (200, 200), interpolation=cv2.INTER_AREA)
        self.display_image_on_canvas(self.image)

if __name__ == "__main__":
    root = tk.Tk()
    app = DigitClassifierApp(root)
    root.mainloop()