document.addEventListener("DOMContentLoaded", () => {
    const messageInput = document.getElementById("message-input");
    const sendButton = document.getElementById("send-button");
    const chatMessages = document.getElementById("chat-messages");
    const contactsContainer = document.getElementById("contacts");
    const chatUsername = document.getElementById("chat-username");
    const chatAvatar = document.getElementById("chat-avatar");
    let stompClient = null;
    let currentUser = null;
    let currentChatId = null;
    let contacts = [];

    // Get current session user from backend
    async function fetchCurrentUser() {
        return fetch("/api/session")
            .then((res) => res.json())
            .then((user) => {
                currentUser = user;
                return user;
            });
    }

    // Connect to WebSocket
    function connectSocket(chatroomId) {
        if (stompClient && stompClient.connected) {
            stompClient.disconnect(() => {
                console.log("Disconnected previous socket");
            });
        }
        const socket = new SockJS("/ws");
        stompClient = Stomp.over(socket);
        stompClient.connect({}, () => {
            console.log("Connecting");
            stompClient.subscribe(`/topic/messages/${chatroomId}`, (message) => {
                const notification = JSON.parse(message.body);
                console.log(notification);
                console.log("connected");
                if (notification.idChatroom === currentChatId) {
                    addMessage(notification.noidungtn, false);
                }
            });
        });
    }

    // Fetch contacts from backend
    function loadContacts() {
        fetch("/users")
            .then((res) => res.json())
            .then((users) => {
                contacts = users;
                contactsContainer.innerHTML = "";
                users.forEach((user, idx) => {
                    const contactDiv = document.createElement("div");
                    contactDiv.classList.add("contact");
                    contactDiv.dataset.userid = user.userid;
                    contactDiv.innerHTML = `
            <img src="https://i.pinimg.com/236x/8a/9d/6e/8a9d6e85a93b8b3a8002896da71882a3.jpg" alt="Contact" class="avatar">
            <div class="contact-info">
              <h4>${user.nickname}</h4>
              <p></p>
            </div>
            <span class="time"></span>
          `;
                    contactDiv.addEventListener("click", () =>
                        selectContact(user, contactDiv),
                    );
                    contactsContainer.appendChild(contactDiv);
                    if (idx === 0) selectContact(user, contactDiv);
                });
            });
    }

    // Fetch messages for a chat
    function loadMessages(chatId) {
        chatMessages.innerHTML = "";
        fetch(`/messages/${chatId}`)
            .then((res) => res.json())
            .then((messages) => {
                messages.forEach((msg) => {
                    addMessage(msg.content, msg.senderId === currentUser.userid);
                });
            })
            .catch(() => {
                // No messages
            });
    }

    // Add message to chat
    function addMessage(message, isSent) {
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

        const messageContent = document.createElement("div");
        messageContent.classList.add("message-content");

        const messageText = document.createElement("p");
        messageText.textContent = message;

        const messageTime = document.createElement("span");
        messageTime.classList.add("time");
        messageTime.textContent = new Date().toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
        });

        messageContent.appendChild(messageText);
        messageContent.appendChild(messageTime);
        messageElement.appendChild(messageContent);

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
            addMessage(message, true);
            messageInput.value = "";
        }
    }

    async function selectContact(user, contactDiv) {
        document
            .querySelectorAll(".contact")
            .forEach((c) => c.classList.remove("active"));
        contactDiv.classList.add("active");
        chatUsername.textContent = user.username;
        chatAvatar.src =
            "https://i.pinimg.com/236x/8a/9d/6e/8a9d6e85a93b8b3a8002896da71882a3.jpg";

        let chatId = null;
        sendButton.disabled = true; // Disable send button by default

        try {
            // Try to get existing chatroom between current user and selected user
            const res = await fetch(`/chatroom/${user.username}`);
            if (!res.ok) throw new Error("No chatroom");
            const data = await res.json();
            chatId = data.idChatroom || data.chatId || data.id; // adapt to your backend response
            user.chatId = chatId; // cache for later
        } catch (e) {
            // If not found, ask user if they want to create a chatroom
            if (
                window.confirm(
                    `No chatroom exists with ${user.username}. Do you want to start a chat?`,
                )
            ) {
                // Create chatroom
                const createRes = await fetch("/chatroom", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ usernameNguoiNhans: [user.username] }),
                });
                if (createRes.ok) {
                    const newChat = await createRes.json();
                    chatId = newChat.idChatroom || newChat.chatId || newChat.id;
                    user.chatId = chatId;
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

        console.log("heelo")
        // Fetch and display messages after finding/creating chatroom
        chatMessages.innerHTML = "";
        fetch(`/messages/${currentChatId}`)
            .then((res) => {
                if (!res.ok) throw new Error("No messages");
                return res.json();
            })
            .then((messages) => {
                console.log(messages, messages.length === 0)
                if (messages.length === 0) {
                    chatMessages.innerHTML =
                        "<div class='message-date'>No messages yet.</div>";
                } else {
                    messages.forEach((msg) => {
                        addMessage(
                            msg.noidungtn,
                            msg.tenNguoiGui === currentUser.username,
                        );
                    });
                }
                connectSocket(currentChatId);
            })
            .catch(() => {
                chatMessages.innerHTML =
                    "<div class='message-date'>No messages yet.</div>";
            });
    }
    // Initialize using current session
    function init() {
        fetchCurrentUser().then(() => {
            connectSocket();
            loadContacts();
        });
    }

    sendButton.addEventListener("click", sendMessage);
    messageInput.addEventListener("keypress", (e) => {
        if (e.key === "Enter") sendMessage();
    });

    init();
});
