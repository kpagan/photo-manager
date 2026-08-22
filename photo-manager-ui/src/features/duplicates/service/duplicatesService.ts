import { requestJson } from '../../../services/appService';
import type { DuplicatesDto } from '../model/DuplicatesDto';

export async function fetchDuplicatesData() {
  return requestJson<DuplicatesDto[]>('/duplicates');
}