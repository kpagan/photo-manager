import { useEffect, useState } from 'react';
import { getImage } from '../service/imageService';

export function useImageLoader(imageId: number, thumbnail: boolean) {
    const [loading, setLoading] = useState<boolean>(true);
    const [imageUrl, setImageUrl] = useState<string | undefined>(undefined);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let isMounted = true;
        const loadImage = async () => {
            try {
                const response = await getImage(imageId, thumbnail);
                if (isMounted) {
                    setImageUrl(response);
                    setError(null);
                }
            } catch (err) {
                if (isMounted) {                    
                    setError(err instanceof Error ? err.message :'Failed to load image');
                    setImageUrl(undefined);
                }
            } finally {
                if (isMounted) {
                    setLoading(false);
                }
            };

        };
        loadImage();

        return () => {
            isMounted = false;
        };
    }, [imageId, thumbnail]);

    return {
        imageUrl,
        loading,
        error,
    };
}