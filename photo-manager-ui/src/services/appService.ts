const DEFAULT_CONTEXT_PATH = '/photos/api';

export async function requestJson<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${DEFAULT_CONTEXT_PATH}${path}`, {
    headers: {
      'Content-Type': 'application/json',
    },
    ...init,
  });

  if (response.status >= 400 && response.status < 600) {
    throw new Error(`Request failed with ${response.status}`);
  }

  return response.json() as Promise<T>;
}

export async function request(path: string, init?: RequestInit): Promise<Response> {
  const response = await fetch(`${DEFAULT_CONTEXT_PATH}${path}`, {
    ...init
  });
  if (response.status >= 400 && response.status < 600) {
    throw new Error(`Request failed with ${response.status}`);
  }
  return response;
}

export async function getImage(imageId: number): Promise<string> {
  const response = await fetch(`${DEFAULT_CONTEXT_PATH}/image/${imageId}`, {
    method: 'GET',
    cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
  });
  const blob = await response.blob()
  return URL.createObjectURL(blob);
}
