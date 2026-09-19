import { useImageLoader } from '../hooks/useImageLoader';

function ImageContainer({ imageId, thumbnail }: { imageId: number; thumbnail: boolean }) {
    const {imageUrl, loading, error} = useImageLoader(imageId, thumbnail);

    return (
        <div className="image-container">
            {loading ? (
                <div className="loading-spinner">Loading...</div>
            ) : (
                error ? (
                    <div className="error-message">Error loading image: {error}</div>
                ) : (
                    <img src={imageUrl} alt={`Image with id ${imageId}`} loading="lazy" decoding="async" />
                )
            )}
        </div>
    );
}

export default ImageContainer;