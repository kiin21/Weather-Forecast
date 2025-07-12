let isResizing = false;
let startX = 0;
let startWidth = 0;

export function initResizablePanel(weatherPanel: HTMLDivElement) {
  let resizeHandle: HTMLDivElement;

  // Create resize handle
  resizeHandle = document.createElement('div');
  resizeHandle.style.cssText = `
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 5px;
    background: #e9ecef;
    cursor: ew-resize;
    z-index: 1001;
    transition: background-color 0.2s ease;
  `;

  resizeHandle.addEventListener('mouseenter', () => {
    resizeHandle.style.background = '#74b9ff';
  });

  resizeHandle.addEventListener('mouseleave', () => {
    if (!isResizing) {
      resizeHandle.style.background = '#e9ecef';
    }
  });

  // Mouse down event
  resizeHandle.addEventListener('mousedown', (e) => {
    isResizing = true;
    startX = e.clientX;
    startWidth = parseInt(document.defaultView?.getComputedStyle(weatherPanel).width || '400', 10);
    document.body.style.cursor = 'ew-resize';
    document.body.style.userSelect = 'none';

    // Add mouse move and mouse up listeners to document
    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);

    e.preventDefault();
  });

  // Mouse move handler
  function handleMouseMove(e: MouseEvent) {
    if (!isResizing) return;

    const deltaX = startX - e.clientX; // Reverse direction since we're resizing from left edge
    const newWidth = startWidth + deltaX;

    // Apply constraints
    const minWidth = 200;
    const maxWidth = 800;
    const constrainedWidth = Math.max(minWidth, Math.min(maxWidth, newWidth));

    weatherPanel.style.width = constrainedWidth + 'px';
  }

  // Mouse up handler
  function handleMouseUp() {
    isResizing = false;
    document.body.style.cursor = '';
    document.body.style.userSelect = '';
    resizeHandle.style.background = '#e9ecef';

    // Remove event listeners
    document.removeEventListener('mousemove', handleMouseMove);
    document.removeEventListener('mouseup', handleMouseUp);
  }

  // Append resize handle to weather panel
  weatherPanel.appendChild(resizeHandle);
}
