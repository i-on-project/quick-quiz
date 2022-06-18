/*
https://davidwalsh.name/cancel-fetch
*/

export const cancellableFetch = (request, opts) => {
    const controller = new AbortController();
    const signal = controller.signal;
  
    return {
      abort: () => controller.abort(),
      ready: fetch(request, { ...opts, signal }),
      signal: signal
    };
}