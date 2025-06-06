document.addEventListener("DOMContentLoaded", () => {
    const messageInput = document.getElementById("message-input");
    const sendButton = document.getElementById("send-button");
    const chatMessages = document.getElementById("chat-messages");
    const contactsContainer = document.getElementById("contacts");
    const chatUsername = document.getElementById("chat-username");
    const chatAvatar = document.getElementById("chat-avatar");
    const createGroupBtn = document.getElementById("create-group-btn");
    const groupModal = document.getElementById("group-modal");
    const closeModal = document.getElementById("close-modal");
    const cancelGroup = document.getElementById("cancel-group");
    const createGroup = document.getElementById("create-group");
    const userList = document.getElementById("user-list");
    const selectedCount = document.getElementById("selected-count");
    const groupNameInput = document.getElementById("group-name");
    const overlay = document.getElementById("imageOverlay");
    const overlayImg = overlay.querySelector("img");
    const closeBtn = overlay.querySelector(".closeBtn");
    const searchIcon = document.querySelector(".search-box i.fa-search");
    const chatroomSearchInput = document.getElementById("searchInput");
    const searchErrorMessage = document.getElementById("search-error-content");
    const searchErrorBox = document.getElementById("search-error-box");

    let stompClient = null;
    let currentUser = null;
    let currentChatId = null;
    let contacts = [];
    let selectedUsers = [];

    closeBtn.addEventListener("click", () => {
        overlay.style.display = "none";
        overlayImg.src = "";
    });

    overlay.addEventListener("click", (e) => {
        if (e.target === overlay) {
            overlay.style.display = "none";
            overlayImg.src = "";
        }
    });

    // Get current session user from backend
    async function fetchCurrentUser() {
        return fetch("/api/session")
            .then((res) => res.json())
            .then((user) => {
                currentUser = user;
                return user;
            })
            .catch(() => {
                // Fallback for demo purposes
                currentUser = {
                    userid: "current_user",
                    username: "current_user",
                    nickname: "Current User",
                };
                return currentUser;
            });
    }

    function searchUsersByNickname(nickname) {
        fetch(`/users/search?nickname=${encodeURIComponent(nickname)}`)
            .then((res) => {
                if (!res.ok) throw new Error("Không tìm thấy người dùng");
                return res.json();
            })
            .then((users) => {
                renderSearchResults(users);
            })
            .catch((err) => {
                console.error("Lỗi tìm kiếm:", err);
                renderSearchResults([]); // hoặc hiển thị lỗi tuỳ ý
            });
    }

    // Connect to WebSocket
    function connectSocket(chatroomId) {
        if (stompClient && stompClient.connected) {
            stompClient.disconnect(() => {
                console.log("Disconnected previous socket");
            });
        }
        const socket = new window.SockJS("/ws");
        stompClient = window.Stomp.over(socket);
        stompClient.connect({}, () => {
            stompClient.subscribe(
                `/topic/messages/${chatroomId}`.trim(),
                (message) => {
                    const notification = JSON.parse(message.body);
                    if (
                        notification.idChatroom.trim() === chatroomId.trim() &&
                        notification.tenNguoiGui.trim() !== currentUser.nickname.trim()
                    ) {
                        addMessage(notification.noidungtn, notification.tenNguoiGui, false);
                    }
                },
            );
        });
    }

    // Fetch contacts from backend
    function loadContacts() {
        fetch("/chatroom")
            .then((res) => res.json())
            .then((chatrooms) => {
                renderContacts(chatrooms)
            })
            .catch(() => {
                // Fallback for demo purposes
                console.log("Using mock data for contacts");
            });
    }

    function renderContacts(chatrooms) {
        contacts = chatrooms;
        contactsContainer.innerHTML = "";

        chatrooms.forEach((chatroom, idx) => {
            const contactDiv = document.createElement("div");
            contactDiv.classList.add("contact");
            contactDiv.dataset.idChatroom = chatroom.idChatroom;

            // Get display name for chatroom
            let chatroomName;
            if (
                chatroom.chatroomMemberIds &&
                chatroom.chatroomMemberIds.length > 2
            ) {
                chatroomName = chatroom.tenChatroom || chatroom.tenchatroom;
            } else {
                const names = (chatroom.tenChatroom || chatroom.tenchatroom || "")
                    .split("&")
                    .map((s) => s.trim());
                chatroomName =
                    names.find(
                        (name) =>
                            name !== currentUser.nickname &&
                            name !== currentUser.username,
                    ) || names[0];
            }
            chatroom.tenchatroom = chatroomName;

            contactDiv.innerHTML = `
      <img src="${
                chatroom.chatroomMemberIds.length > 2
                    ? "https://images-ext-1.discordapp.net/external/n2SOR1uMNvtHfq-5ZJSUDUf7KWmceHQlEEpGfBh-2QU/https/www.cmu.edu/staff-council/images/icons/reps-icon.png?format=webp&quality=lossless"
                    : "https://i.pinimg.com/236x/8a/9d/6e/8a9d6e85a93b8b3a8002896da71882a3.jpg"
            }" alt="Contact" class="avatar">
      <div class="contact-info">
        <h4>${chatroomName}</h4>
        <p></p>
      </div>
      <span class="time"></span>
    `;
            contactDiv.addEventListener("click", () =>
                selectContact(chatroom, contactDiv),
            );
            contactsContainer.appendChild(contactDiv);
            if (idx === 0) selectContact(chatroom, contactDiv);
        });
    }


    // //Tim kiém phòng chat
    // chatroomSearchInput.addEventListener("input", function (e) {
    //     if (e.key === "Enter") {
    //         const keyword = this.value;
    //         if (keyword.length === 0) {
    //             loadContacts(); // Nếu trống thì hiển thị lại danh sách đầy đủ
    //             return;
    //         }
    //
    //         fetch(`/chatroom/${encodeURIComponent(username)}`)
    //             .then((res) => {
    //                 if (!res.ok) throw new Error("Không thể tìm chatroom");
    //                 return res.json();
    //             })
    //             .then((chatrooms) => {
    //                 renderContacts(chatrooms);
    //             })
    //             .catch((err) => {
    //                 console.error("Lỗi khi tìm chatroom:", err);
    //             });
    //     }
    // });

    searchIcon.addEventListener("click", function () {
        const tenchatroom = chatroomSearchInput.value;
        console.log(tenchatroom);
        if (tenchatroom.length === 0) {
            searchErrorBox.style.display = "none";
            loadContacts(); // Nếu trống thì hiển thị lại danh sách đầy đủ
            return;
        }

        fetch(`/chatroom/search/${tenchatroom}`)
            .then((res) => {
                if (!res.ok) {
                    throw new Error("Không thể tìm chatroom");
                }
                return res.json();
            })
            .then((chatrooms) => {
                console.log(chatrooms.length);
                console.log(chatrooms);
                if (chatrooms.length === 0){
                    console.log("Vao dc cau if");
                    searchErrorMessage.innerText = "Không tìm thấy chatroom nào";
                    searchErrorBox.style.display = "block";
                    searchErrorMessage.style.color = "black";
                    searchErrorMessage.style.textAlign = "center";
                    searchErrorMessage.style.marginTop = "20px";
                    contactsContainer.innerHTML = "";
                }
                else {
                    searchErrorBox.style.display = "none";
                    renderContacts(chatrooms);
                }
            })
            .catch((err) => {
                console.error("Lỗi khi tìm chatroom:", err);
            });
    })


    // Add message to chat
    function addMessage(message, sender, isSent, originalFilename = null) {
        const messageElement = document.createElement("div");
        messageElement.classList.add("message");
        messageElement.classList.add(isSent ? "sent" : "received");

        if (!isSent) {
            const avatar = document.createElement("img");
            avatar.src =
                "https://i.pinimg.com/236x/8a/9d/6e/8a9d6e85a93b8b3a8002896da71882a3.jpg";
            avatar.alt = "Contact";
            avatar.classList.add("avatar");
            messageElement.appendChild(avatar);
        }

        const messageDiv = document.createElement("div");

        // Add sender name for group chats
        if (sender && !isSent) {
            const messageSender = document.createElement("span");
            messageSender.classList.add("sender");
            messageSender.textContent = sender;
            messageSender.style.fontSize = "0.7em";
            messageSender.style.color = "#65676b";
            messageSender.style.display = "block";
            messageSender.style.marginBottom = "2px";
            messageDiv.appendChild(messageSender);
        }

        const messageContent = document.createElement("div");
        messageContent.classList.add("message-content");

        const messageText = document.createElement("p");

        const isImage = message.match(/\.(jpg|jpeg|png|gif|bmp)$/i); // Kiểm tra xem có phải ảnh không
        const isFile = message.includes("/uploads/files/") && !isImage;

        if (isImage) {
            const img = document.createElement("img");
            img.src = message;
            img.alt = "Image";
            img.style.maxWidth = "400px";
            img.style.maxHeight = "400px";
            img.style.borderRadius = "10px";
            img.style.display = "block";
            img.style.background = "transparent"; // 👈 Trong suốt
            messageContent.style.background = "transparent"; // 👈 Trong suốt nền thẻ chứa
            messageContent.appendChild(img);

            img.addEventListener("click", () => {
                overlay.style.display = "flex";
                overlayImg.src = img.src;
            });
        } else if (isFile) {
            const fileIcon = document.createElement("span");
            fileIcon.innerHTML = '<i class="fas fa-file-alt"></i>';
            fileIcon.style.fontSize = "1.2em";
            fileIcon.style.marginRight = "5px";

            const fileName = originalFilename || decodeURIComponent(message.split("/").pop());

            const fileLink = document.createElement("a");
            fileLink.href = message;
            fileLink.target = "_blank";
            fileLink.download = fileName;
            fileLink.textContent = fileName;
            fileLink.style.color = "#333";
            fileLink.style.fontWeight = "bold";
            fileLink.style.textDecoration = "none";

            const fileRow = document.createElement("div");
            fileRow.style.display = "flex";
            fileRow.style.alignItems = "center";
            fileRow.appendChild(fileIcon);
            fileRow.appendChild(fileLink);

            messageContent.appendChild(fileRow);
            messageContent.style.background = "#e6e6e6";
            messageContent.style.borderRadius = "8px";
            messageContent.style.padding = "8px 12px";
        } else {
            messageText.textContent = message;
            messageContent.appendChild(messageText);
        }

        messageDiv.appendChild(messageContent);
        messageElement.appendChild(messageDiv);

        chatMessages.appendChild(messageElement);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    // Send message via socket
    function sendMessage() {
        const message = messageInput.value.trim();
        if (message && stompClient && currentChatId) {
            stompClient.send(
                "/app/chat",
                {},
                JSON.stringify({
                    idChatroom: currentChatId.trim(),
                    noidungtn: message,
                }),
            );
            addMessage(message, "", true);
            messageInput.value = "";
        }
    }

    //Upload file về server
    async function uploadAndSendFile(file, isImage) {
        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await fetch('/upload', {
                method: 'POST',
                body: formData,
            });

            if (response.ok) {
                const attachment = await response.json();
                const fileUrl = attachment.imageUrl || attachment.fileUrl;
                const originalFilename = attachment.originalFilename;// Lấy URL từ imageUrl hoặc fileUrl
                console.log('File uploaded:', fileUrl);
                if (stompClient && currentChatId) {
                    stompClient.send(
                        "/app/chat",
                        {},
                        JSON.stringify({
                            idChatroom: currentChatId.trim(),
                            noidungtn: fileUrl,
                        }),
                    );
                    addMessage(fileUrl, "", true, originalFilename); // Hiển thị ngay với fileUrl
                }
            } else {
                alert('Failed to upload file');
            }
        } catch (err) {
            console.error('Error uploading file:', err);
            alert('Error uploading file');
        }
    }

    async function selectContact(chatroom, contactDiv) {
        document
            .querySelectorAll(".contact")
            .forEach((c) => c.classList.remove("active"));
        contactDiv.classList.add("active");
        chatUsername.textContent = chatroom.tenchatroom;
        chatAvatar.src =
            chatroom.chatroomMemberIds.length > 2
                ? "https://images-ext-1.discordapp.net/external/n2SOR1uMNvtHfq-5ZJSUDUf7KWmceHQlEEpGfBh-2QU/https/www.cmu.edu/staff-council/images/icons/reps-icon.png?format=webp&quality=lossless"
                : "https://i.pinimg.com/236x/8a/9d/6e/8a9d6e85a93b8b3a8002896da71882a3.jpg";

        let chatId = null;
        sendButton.disabled = true; // Disable send button by default

        try {
            // Try to get existing chatroom
            if (!chatroom.idChatroom) throw new Error("No chatroom");
            chatId = chatroom.idChatroom;
        } catch (e) {
            // If not found, ask user if they want to create a chatroom
            if (
                window.confirm(
                    `No chatroom exists with ${chatroom.tenchatroom}. Do you want to start a chat?`,
                )
            ) {
                // Create chatroom
                const createRes = await fetch("/chatroom", {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({
                        useridNguoiNhans: chatroom.chatroomMemberIds,
                    }),
                });
                if (createRes.ok) {
                    const newChat = await createRes.json();
                    chatId = newChat.idChatroom || newChat.chatId || newChat.id;
                    chatroom.chatId = chatId;
                } else {
                    alert("Failed to create chatroom.");
                    sendButton.disabled = true;
                    chatMessages.innerHTML = "";
                    return;
                }
            } else {
                sendButton.disabled = true;
                chatMessages.innerHTML = "";
                return;
            }
        }
        currentChatId = chatId;
        sendButton.disabled = false; // Enable send button if chatroom exists or is created

        // Fetch and display messages after finding/creating chatroom
        chatMessages.innerHTML = "";
        fetch(`/messages/${currentChatId}`)
            .then((res) => {
                if (!res.ok) throw new Error("No messages");
                return res.json();
            })
            .then((messages) => {
                if (messages.length === 0) {
                    chatMessages.innerHTML =
                        "<div class='message-date'>No messages yet.</div>";
                } else {
                    messages.forEach((msg) => {
                        addMessage(
                            msg.noidungtn,
                            msg.tenNguoiGui,
                            msg.tenNguoiGui === currentUser.username,
                        );
                    });
                }
                connectSocket(currentChatId);
            })
            .catch(() => {
                chatMessages.innerHTML =
                    "<div class='message-date'>No messages yet.</div>";
                connectSocket(currentChatId);
            });
    }

    // Group chat functionality
    function showGroupModal() {
        selectedUsers = [];
        updateSelectedCount();
        populateUserList();
        groupModal.style.display = "block";
        groupNameInput.value = "";
        updateCreateButton();
    }

    function hideGroupModal() {
        groupModal.style.display = "none";
        selectedUsers = [];
        updateSelectedCount();
    }

    async function populateUserList() {
        userList.innerHTML = "";
        const fetchedUsers = await fetch("/users")
            .then((res) => res.json())
            .then((users) => {
                return users;
            })
            .catch(() => {
                return [];
            });

        // Filter out current user from the list
        const availableUsers = fetchedUsers.filter(
            (user) => user.userid !== currentUser.userid,
        );

        availableUsers.forEach((user) => {
            const userItem = document.createElement("div");
            userItem.classList.add("user-item");

            userItem.innerHTML = `
        <input type="checkbox" id="user-${user.userid}" data-userid="${user.userid}">
        <img src="https://i.pinimg.com/236x/8a/9d/6e/8a9d6e85a93b8b3a8002896da71882a3.jpg" alt="${user.nickname}" class="user-avatar">
        <div class="user-info">
          <h5>${user.nickname}</h5>
          <p>@${user.username}</p>
        </div>
      `;

            const checkbox = userItem.querySelector('input[type="checkbox"]');
            checkbox.addEventListener("change", (e) => {
                if (e.target.checked) {
                    selectedUsers.push(user.userid);
                } else {
                    selectedUsers = selectedUsers.filter((id) => id !== user.userid);
                }
                updateSelectedCount();
                updateCreateButton();
            });

            // Make the entire item clickable
            userItem.addEventListener("click", (e) => {
                if (e.target.type !== "checkbox") {
                    checkbox.checked = !checkbox.checked;
                    checkbox.dispatchEvent(new Event("change"));
                }
            });

            userList.appendChild(userItem);
        });
    }

    function updateSelectedCount() {
        selectedCount.textContent = `${selectedUsers.length} selected`;
    }

    function updateCreateButton() {
        createGroup.disabled = selectedUsers.length < 2;
    }

    async function createGroupChat() {
        if (selectedUsers.length < 2) return;

        const groupName =
            groupNameInput.value.trim() ||
            `Group with ${selectedUsers.length} members`;

        console.log("Creating group chat:");
        console.log("Group name:", groupName);
        console.log("Selected user IDs:", selectedUsers);

        selectedUsers.push(currentUser.userid);

        await fetch("/chatroom", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                tenchatroom: groupName,
                useridNguoiNhans: selectedUsers,
            }),
        })
            .then((res) => res.json())
            .then((data) => {
                console.log("Group created:", data);
                hideGroupModal();
                loadContacts(); // Refresh contacts list
            })
            .catch((error) => {
                console.error("Error creating group:", error);
                alert("Failed to create group chat");
            });

        hideGroupModal();
    }

    // Event listeners for group modal
    createGroupBtn.addEventListener("click", showGroupModal);
    closeModal.addEventListener("click", hideGroupModal);
    cancelGroup.addEventListener("click", hideGroupModal);
    createGroup.addEventListener("click", createGroupChat);

    // Close modal when clicking outside
    window.addEventListener("click", (e) => {
        if (e.target === groupModal) {
            hideGroupModal();
        }
    });

    // Initialize using current session
    function init() {
        fetchCurrentUser().then(() => {
            loadContacts();
        });
    }

    // Add event listeners for attachment image
    document
        .querySelector(".input-actions .fa-image")
        .addEventListener("click", () => {
            // Create file input for images
            const imageInput = document.createElement("input");
            imageInput.type = "file";
            imageInput.accept = "image/*";
            imageInput.onchange = (e) => {
                const file = e.target.files[0];
                if (file) {
                    console.log("Image selected:", file.name);
                    // Here you can add image upload functionality
                    uploadAndSendFile(file, true)
                }
            };
            imageInput.click();
        });

    // Add event listeners for attachment file
    document
        .querySelector(".input-actions .fa-paperclip")
        .addEventListener("click", () => {
            // Create file input for attachments
            const fileInput = document.createElement("input");
            fileInput.type = "file";
            fileInput.onchange = (e) => {
                const file = e.target.files[0];
                if (file) {
                    console.log("File selected:", file.name);
                    // Here you can add file upload functionality
                    uploadAndSendFile(file, true);
                }
            };
            fileInput.click();
        });

    sendButton.addEventListener("click", sendMessage);
    messageInput.addEventListener("keypress", (e) => {
        if (e.key === "Enter") sendMessage();
    });

    const logoutBtn = document.getElementById("logout-btn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", async () => {
            await fetch("/api/logout", {
                method: "POST",
                credentials: "same-origin",
            });
            window.location.href = "/";
        });
    }
    init();
});
