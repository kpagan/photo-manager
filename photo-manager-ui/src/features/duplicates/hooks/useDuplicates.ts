import { useCallback, useEffect, useLayoutEffect, useRef, useState } from 'react';
import type { DuplicatesDto } from '../model/DuplicatesDto';
import { fetchDuplicatesData } from '../service/duplicatesService';

const DEFAULT_PAGE_SIZE = 10;

export function useDuplicates(pageSize: number = DEFAULT_PAGE_SIZE) {
  const [duplicates, setDuplicates] = useState<DuplicatesDto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [loadingMore, setLoadingMore] = useState<boolean>(false);
  const [hasMore, setHasMore] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [totalElements, setTotalElements] = useState<number>(0);

  const sentinelRef = useRef<HTMLDivElement | null>(null);
  const scrollPosRef = useRef<number>(0);
  const prevDuplicatesLengthRef = useRef<number>(duplicates.length);

  const currentPageRef = useRef<number>(0);
  const isLoadingRef = useRef<boolean>(false);
  const hasMoreRef = useRef<boolean>(hasMore);
  const lastLoadTimeRef = useRef<number>(0);
  const abortControllerRef = useRef<AbortController | null>(null);
  const COOLDOWN_MS = 300;

  const loadInitialPage = useCallback(async () => {
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
    const controller = new AbortController();
    abortControllerRef.current = controller;

    setLoading(true);
    setError(null);
    isLoadingRef.current = true;
    currentPageRef.current = 0;

    try {
      const response = await fetchDuplicatesData(0, pageSize, controller.signal);
      setDuplicates(response.content ?? []);
      const more = !response.last && (response.number + 1 < response.totalPages);
      setHasMore(more);
      hasMoreRef.current = more;
      setTotalElements(response.totalElements ?? 0);
      currentPageRef.current = 0;
      lastLoadTimeRef.current = Date.now();
    } catch (err: unknown) {
      if (err instanceof Error && err.name === 'AbortError') {
        return;
      }
      setError(err instanceof Error ? err.message : 'Failed to load duplicates');
    } finally {
      if (!controller.signal.aborted) {
        setLoading(false);
        isLoadingRef.current = false;
      }
    }
  }, [pageSize]);

  const loadMore = useCallback(async () => {
    const now = Date.now();
    if (isLoadingRef.current || !hasMoreRef.current || now - lastLoadTimeRef.current < COOLDOWN_MS) {
      return;
    }

    const nextPage = currentPageRef.current + 1;
    setLoadingMore(true);
    setError(null);
    isLoadingRef.current = true;
    lastLoadTimeRef.current = now;

    try {
      const response = await fetchDuplicatesData(nextPage, pageSize);
      setDuplicates((prev) => [...prev, ...(response.content ?? [])]);
      const more = !response.last && (response.number + 1 < response.totalPages);
      setHasMore(more);
      hasMoreRef.current = more;
      setTotalElements(response.totalElements ?? 0);
      currentPageRef.current = nextPage;
      lastLoadTimeRef.current = Date.now();
    } catch (err: unknown) {
      if (err instanceof Error && err.name === 'AbortError') {
        return;
      }
      setError(err instanceof Error ? err.message : 'Failed to load more duplicates');
    } finally {
      setLoadingMore(false);
      isLoadingRef.current = false;
    }
  }, [pageSize]);

  useEffect(() => {
    loadInitialPage();
    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [loadInitialPage]);

  // Preserve scroll position when new duplicate items are rendered
  useLayoutEffect(() => {
    if (duplicates.length > prevDuplicatesLengthRef.current && prevDuplicatesLengthRef.current > 0) {
      window.scrollTo(0, scrollPosRef.current);
    }
    prevDuplicatesLengthRef.current = duplicates.length;
  }, [duplicates.length]);

  const loadMoreRef = useRef(loadMore);
  useEffect(() => {
    const sentinel = sentinelRef.current;
    if (!sentinel) return;

    const observer = new IntersectionObserver(
      (entries) => {
        const entry = entries[0];
        if (entry && entry.isIntersecting && hasMoreRef.current && !isLoadingRef.current) {
          scrollPosRef.current = window.scrollY;
          loadMoreRef.current();
        }
      },
      {
        root: null,
        rootMargin: '0px',
        threshold: 0,
      }
    );

    observer.observe(sentinel);

    return () => {
      observer.disconnect();
    };
  }, [duplicates.length]);

  return {
    duplicates,
    loading,
    loadingMore,
    hasMore,
    error,
    totalElements,
    loadMore,
    refresh: loadInitialPage,
    sentinelRef
  };
}