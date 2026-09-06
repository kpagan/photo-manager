import { useImageLoader } from '../hooks/useImageLoader';

function ImageContainer({ imageId }: { imageId: number }) {
    const {imageUrl, loading, error} = useImageLoader(imageId);

    return (
        <div className="image-container">
            {loading ? (
                <div className="loading-spinner">Loading...</div>
            ) : (
                error ? (
                    <div className="error-message">Error loading image: {error}</div>
                ) : (
                    <img src={imageUrl} alt={`Image with id ${imageId}`} />
                )
            )}
        </div>
    );
}

export default ImageContainer;