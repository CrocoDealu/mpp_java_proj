(window as any).global = window;
(window as any).process = { env: { DEBUG: undefined } };
if (!(window as any).crypto) {
  (window as any).crypto = window.crypto || {};
}
