const VITE_BACKEND_URL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';

export function fetchChatAI(message: string): Promise<string> {
  const url = `${VITE_BACKEND_URL}/chat`;

  return fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ message })
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.text();
    });
}

export function fetchChatAIStream(message: string): Promise<ReadableStream> {
  const url = `${VITE_BACKEND_URL}/chat/stream`;

  return fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'text/event-stream',
    },
    body: JSON.stringify({ message })
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      if (!response.body) {
        throw new Error('Response body is null');
      }
      return response.body;
    });
}

export async function* streamChatResponse(message: string): AsyncGenerator<string, void, unknown> {
  try {
    const stream = await fetchChatAIStream(message);
    const reader = stream.getReader();
    const decoder = new TextDecoder();

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      const chunk = decoder.decode(value, { stream: true });
      const lines = chunk.split('\n');

      for (const line of lines) {
        if (line.trim()) {
          yield line.trim();
        }
      }
    }
  } catch (error) {
    console.error('Stream error:', error);
    throw error;
  }
}


