export function createExpandableText(text: string, maxLength: number = 80): string {
  if (!text || text.length <= maxLength) {
    return `<span class="weather-description-text">${text || 'Waiting for weather summary...'}</span>`;
  }

  const shortText = text.substring(0, maxLength);
  const remainingText = text.substring(maxLength);
  const uniqueId = `desc-${Math.random().toString(36).substr(2, 9)}`;

  return `
    <div class="expandable-text">
      <span class="weather-description-text">
        ${shortText}<span id="remaining-${uniqueId}" class="remaining-text" style="display: none;">${remainingText}</span><span id="ellipsis-${uniqueId}">...</span>
      </span>
      <button 
        id="toggle-${uniqueId}" 
        class="show-more-btn" 
        onclick="toggleDescription('${uniqueId}')"
        style="
          background: none; 
          border: none; 
          color: #74b9ff; 
          cursor: pointer; 
          font-size: 0.8em; 
          padding: 2px 4px; 
          margin-left: 5px;
          text-decoration: underline;
        "
      >
        Show more
      </button>
    </div>
  `;
}

export function toggleDescription(id: string) {
  const remainingText = document.getElementById(`remaining-${id}`);
  const ellipsis = document.getElementById(`ellipsis-${id}`);
  const toggleBtn = document.getElementById(`toggle-${id}`) as HTMLButtonElement;

  if (remainingText && ellipsis && toggleBtn) {
    if (remainingText.style.display === 'none') {
      // Show full text
      remainingText.style.display = 'inline';
      ellipsis.style.display = 'none';
      toggleBtn.textContent = 'Show less';
    } else {
      // Show truncated text
      remainingText.style.display = 'none';
      ellipsis.style.display = 'inline';
      toggleBtn.textContent = 'Show more';
    }
  }
}

export const sleep = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));
