import { useEffect, useState } from 'react';
import { mockDuplicatesData, type DuplicatesDto } from '../model/DuplicatesDto';

export function useDuplicates() {
  const [duplicates, setDuplicates] = useState<DuplicatesDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

    useEffect(() => {
    let isMounted = true;

    const loadDuplicates = async () => {
        try {
            // Simulate an API call with a delay
            let duplicates: DuplicatesDto[] = [];
            await new Promise(resolve => setTimeout(() => {
                duplicates = mockDuplicatesData;
                resolve(duplicates);
            }, 1000));
            if (isMounted) {
                setDuplicates(duplicates);
                setLoading(false);
            }
        } catch (err) {
            if (isMounted) {
                setError('Failed to load duplicates');
                setLoading(false);
            }
        }
    };

    loadDuplicates();

    return () => {
        isMounted = false;
    };
    }, []);

    return { 
        duplicates, 
        loading, 
        error 
    };
}