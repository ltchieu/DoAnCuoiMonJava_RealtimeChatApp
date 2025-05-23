document.addEventListener("DOMContentLoaded", () => {
  const messageInput = document.getElementById("message-input")
  const sendButton = document.getElementById("send-button")
  const chatMessages = document.getElementById("chat-messages")
  const contacts = document.querySelectorAll(".contact")

  // Sample responses for demo
  const botResponses = [
    "That's interesting! Tell me more.",
    "I see what you mean.",
    "I'm not sure I understand. Could you explain?",
    "That's great to hear!",
    "I've been thinking about that too.",
    "Let's discuss this further tomorrow.",
    "Do you have any plans for the weekend?",
    "Have you seen that new movie everyone's talking about?",
  ]

  // Function to get current time
  function getCurrentTime() {
    const now = new Date()
    let hours = now.getHours()
    let minutes = now.getMinutes()
    const ampm = hours >= 12 ? "PM" : "AM"

    hours = hours % 12
    hours = hours ? hours : 12
    minutes = minutes < 10 ? "0" + minutes : minutes

    return `${hours}:${minutes} ${ampm}`
  }

  // Function to add a new message
  function addMessage(message, isSent) {
    const messageElement = document.createElement("div")
    messageElement.classList.add("message")
    messageElement.classList.add(isSent ? "sent" : "received")

    const currentTime = getCurrentTime()

    if (!isSent) {
      const avatar = document.createElement("img")
      avatar.src = "/placeholder.svg?height=30&width=30"
      avatar.alt = "Contact"
      avatar.classList.add("avatar")
      messageElement.appendChild(avatar)
    }

    const messageContent = document.createElement("div")
    messageContent.classList.add("message-content")

    const messageText = document.createElement("p")
    messageText.textContent = message

    const messageTime = document.createElement("span")
    messageTime.classList.add("time")
    messageTime.textContent = currentTime

    messageContent.appendChild(messageText)
    messageContent.appendChild(messageTime)
    messageElement.appendChild(messageContent)

    chatMessages.appendChild(messageElement)

    // Scroll to the bottom of the chat
    chatMessages.scrollTop = chatMessages.scrollHeight
  }

  // Function to send a message
  function sendMessage() {
    const message = messageInput.value.trim()

    if (message !== "") {
      // Add user message
      addMessage(message, true)
      messageInput.value = ""

      // Simulate typing indicator
      setTimeout(() => {
        // Add bot response after a delay
        const randomResponse = botResponses[Math.floor(Math.random() * botResponses.length)]
        addMessage(randomResponse, false)
      }, 1000)
    }
  }

  // Send message on button click
  sendButton.addEventListener("click", () => {
    sendMessage()
  })

  // Send message on Enter key
  messageInput.addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
      sendMessage()
    }
  })

  // Switch between contacts
  contacts.forEach((contact) => {
    contact.addEventListener("click", function () {
      // Remove active class from all contacts
      contacts.forEach((c) => c.classList.remove("active"))
      // Add active class to clicked contact
      this.classList.add("active")

      // Update chat header with contact info
      const contactName = this.querySelector("h4").textContent
      document.querySelector(".chat-user h3").textContent = contactName

      // You could load different chat history here in a real app
    })
  })
})

