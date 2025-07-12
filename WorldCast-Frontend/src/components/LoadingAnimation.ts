export function createLoadingAnimation(): HTMLElement {
  const container = document.createElement('div');
  container.className = 'loading-container';

  container.innerHTML = `
    <div class="loading-spinner"></div>
    <div class="loading-text">Fetching Weather Data</div>
    <div class="loading-subtext">Connecting to weather services...</div>`;

  return container;
}

export function updateLoadingMessage(container: HTMLElement, message: string, subtext: string = '') {
  const loadingText = container.querySelector('.loading-text') as HTMLElement;
  const loadingSubtext = container.querySelector('.loading-subtext') as HTMLElement;

  if (loadingText) loadingText.textContent = message;
  if (loadingSubtext) loadingSubtext.textContent = subtext;
}
