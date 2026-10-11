function selectOption(element, value) {
    const cards = element.parentElement.querySelectorAll('.option-card');
    cards.forEach(card => card.classList.remove('selected'));
    element.classList.add('selected');

    const hiddenInput = document.getElementById('regimeInput');
    if(hiddenInput) hiddenInput.value = value;
}

function sendPrompt(buttonElement) {
    const text = buttonElement.innerText;
    const input = document.getElementById('messageInputField');
    if(input) {
        input.value = text;
        performSendMessage();
    }
}

function performSendMessage() {
    const input = document.getElementById('messageInputField');
    if(!input || input.value.trim() === "") return;

    const chatRegistry = document.getElementById('chatRegistry');
    const newMsg = document.createElement('div');
    newMsg.className = 'msg-bubble';
    newMsg.style.alignSelf = 'flex-end';
    newMsg.style.backgroundColor = '#295241';
    newMsg.style.color = '#FFFFFF';

    const now = new Date();
    const timeString = now.getHours().toString().padStart(2, '0') + ':' + now.getMinutes().toString().padStart(2, '0');

    newMsg.innerHTML = `${input.value}<div class="msg-time" style="color: rgba(255,255,255,0.6);">${timeString}</div>`;

    chatRegistry.appendChild(newMsg);
    input.value = "";
    chatRegistry.scrollTop = chatRegistry.scrollHeight;
}
