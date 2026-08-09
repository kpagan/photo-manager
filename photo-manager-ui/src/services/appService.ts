export type ScanResponse = {
  message: string;
  jobId?: string;
  status?: string;
};

const DEFAULT_CONTEXT_PATH = '/photos/api';

export async function requestJson<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${DEFAULT_CONTEXT_PATH}${path}`, {
    headers: {
      'Content-Type': 'application/json',
    },
    ...init,
  });

  if (!response.ok) {
    throw new Error(`Request failed with ${response.status}`);
  }

  return response.json() as Promise<T>;
}

export async function startFolderScan(): Promise<ScanResponse> {
  return requestJson<ScanResponse>('/folder-scan/start', {
    method: 'POST',
  });
}
