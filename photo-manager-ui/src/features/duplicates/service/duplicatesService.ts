import { requestJson } from '../../../services/appService';
import type { DuplicatesDto, PageResponse } from '../model/DuplicatesDto';

export async function fetchDuplicatesData(page: number = 0, size: number = 10, signal?: AbortSignal): Promise<PageResponse<DuplicatesDto>> {
  return requestJson<PageResponse<DuplicatesDto>>(`/duplicates?page=${page}&size=${size}`, { signal });
}