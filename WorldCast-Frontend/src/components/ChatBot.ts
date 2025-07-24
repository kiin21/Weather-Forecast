import { streamChatResponse } from '../services/chatService.js';

export interface ChatMessage {
  id: string;
  content: string;
  isUser: boolean;
  timestamp: Date;
}

export class ChatBot {
  private container: HTMLElement;
  private messages: ChatMessage[] = [];
  private isOpen: boolean = false;

  constructor() {
    this.container = this.createChatContainer();
    document.body.appendChild(this.container);
    // Setup event listeners after container is added to DOM
    setTimeout(() => this.setupEventListeners(), 0);
  }

  private createChatContainer(): HTMLElement {
    const container = document.createElement('div');
    container.className = 'chatbot-container';
    container.innerHTML = `
      <div class="chatbot-toggle">
        <button id="chatbot-toggle-btn" class="chatbot-toggle-btn">
          <span class="chat-icon">💬</span>
          <span class="close-icon">✕</span>
        </button>
      </div>
      
      <div class="chatbot-window" id="chatbot-window">
        <div class="chatbot-header">
          <h3>Weather Assistant</h3>
          <span class="chatbot-status">Online</span>
        </div>
        
        <div class="chatbot-messages" id="chatbot-messages">
          <div class="welcome-message">
            <div class="bot-message">
              <span class="bot-avatar">🤖</span>
              <div class="message-content">
                Hello! I'm your weather assistant. Ask me about weather conditions, forecasts, or click on the map to get weather information for any location!
              </div>
            </div>
          </div>
        </div>
        
        <div class="chatbot-input">
          <div class="input-container">
            <input type="text" id="chatbot-input" placeholder="Ask about weather..." />
            <button id="chatbot-send" class="send-btn">
              <span>📤</span>
            </button>
          </div>
        </div>
      </div>
    `;

    return container;
  }

  private setupEventListeners(): void {
    // Toggle chatbot with more robust element selection
    const toggleBtn = this.container.querySelector('#chatbot-toggle-btn') as HTMLButtonElement;
    if (toggleBtn) {
      toggleBtn.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        this.toggleChat();
      });
    } else {
      console.error('Chatbot toggle button not found');
    }

    // Send message with more robust element selection
    const sendBtn = this.container.querySelector('#chatbot-send') as HTMLButtonElement;
    const input = this.container.querySelector('#chatbot-input') as HTMLInputElement;

    if (sendBtn) {
      sendBtn.addEventListener('click', (e) => {
        e.preventDefault();
        this.sendMessageStream();
      });
    }

    if (input) {
      input.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
          e.preventDefault();
          this.sendMessageStream();
        }
      });
    }
  }

  private toggleChat(): void {
    this.isOpen = !this.isOpen;
    const window = this.container.querySelector('#chatbot-window') as HTMLElement;
    const toggleBtn = this.container.querySelector('#chatbot-toggle-btn') as HTMLElement;

    if (window && toggleBtn) {
      if (this.isOpen) {
        window.classList.add('open');
        toggleBtn.classList.add('active');
      } else {
        window.classList.remove('open');
        toggleBtn.classList.remove('active');
      }
    }
  }

  private async sendMessageStream(): Promise<void> {
    const input = this.container.querySelector('#chatbot-input') as HTMLInputElement;
    if (!input) return;

    const message = input.value.trim();
    if (!message) return;

    // Add user message
    this.addMessage(message, true);
    input.value = '';

    // Show typing indicator
    this.showTypingIndicator();

    try {
      this.hideTypingIndicator();

      // Create a message element for streaming response
      const messageId = Math.random().toString(36).substr(2, 9);
      const streamMessage: ChatMessage = {
        id: messageId,
        content: '',
        isUser: false,
        timestamp: new Date()
      };

      this.messages.push(streamMessage);
      this.renderMessage(streamMessage);

      // Stream the response
      const messageElement = this.container.querySelector(`[data-message-id="${messageId}"] .message-content`) as HTMLElement;

      for await (const chunk of streamChatResponse(message)) {
        streamMessage.content += chunk.replace(/^data:\s*/, '') + " ";
        if (messageElement) {
          messageElement.textContent = streamMessage.content;
        }

        // Auto-scroll to bottom
        const messagesContainer = this.container.querySelector('#chatbot-messages') as HTMLElement;
        if (messagesContainer) {
          messagesContainer.scrollTo(0, messagesContainer.scrollHeight);
        }
      }
    } catch (error: any) {
      this.hideTypingIndicator();
      console.error('Chat stream error:', error);
      this.addMessage('Sorry, I encountered an error with the streaming service. Please try again.', false);
    }
  }

  private addMessage(content: string, isUser: boolean): void {
    const message: ChatMessage = {
      id: Math.random().toString(36).substr(2, 9),
      content,
      isUser,
      timestamp: new Date()
    };

    this.messages.push(message);
    this.renderMessage(message);
  }

  private renderMessage(message: ChatMessage): void {
    const messagesContainer = this.container.querySelector('#chatbot-messages') as HTMLElement;
    if (!messagesContainer) return;

    const messageElement = document.createElement('div');
    messageElement.className = message.isUser ? 'user-message' : 'bot-message';
    messageElement.setAttribute('data-message-id', message.id);

    if (message.isUser) {
      messageElement.innerHTML = `
        <div class="message-content">${message.content}</div>
        <span class="user-avatar">👤</span>
      `;
    } else {
      messageElement.innerHTML = `
        <span class="bot-avatar">🤖</span>
        <div class="message-content">${message.content}</div>
      `;
    }

    messagesContainer.appendChild(messageElement);
    messagesContainer.scrollTo(0, messagesContainer.scrollHeight);
  }

  private showTypingIndicator(): void {
    const messagesContainer = this.container.querySelector('#chatbot-messages') as HTMLElement;
    if (!messagesContainer) return;

    const typingElement = document.createElement('div');
    typingElement.className = 'bot-message typing-indicator';
    typingElement.id = 'typing-indicator';
    typingElement.innerHTML = `
      <span class="bot-avatar">🤖</span>
      <div class="message-content">
        <div class="typing-dots">
          <span></span><span></span><span></span>
        </div>
      </div>
    `;

    messagesContainer.appendChild(typingElement);
    messagesContainer.scrollTo(0, messagesContainer.scrollHeight);
  }

  private hideTypingIndicator(): void {
    const typingElement = this.container.querySelector('#typing-indicator') as HTMLElement;
    if (typingElement) {
      typingElement.remove();
    }
  }

  // Public method to send weather updates
  public sendWeatherUpdate(location: string, weatherData: any): void {
    const message = `I found weather data for ${location}! Current temperature is ${weatherData.current?.temperature}°C with ${weatherData.current?.weatherDescription}. Check the weather panel for more details!`;
    this.addMessage(message, false);
  }
}
