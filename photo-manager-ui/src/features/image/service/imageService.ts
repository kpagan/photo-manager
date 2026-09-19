import { request } from '../../../services/appService';

export async function getImage(imageId: number, thumbnail: boolean): Promise<string> {
  const path = thumbnail ? `/image/${imageId}/thumbnail` : `/image/${imageId}`;
  const response = await request(path, {
    method: 'GET',
    cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
  });
  const blob = await response.blob();
  return URL.createObjectURL(blob);
}
