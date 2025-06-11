import React, { useState } from "react";

const AvatarConFallback = ({ src, fallbackText, className, onClick }) => {
    const [imgError, setImgError] = useState(false);

    const getInitials = (text) => {
        if (!text) return "U";
        const words = text.trim().split(" ");
        if (words.length === 1) {
            return words[0].charAt(0).toUpperCase();
        } else {
            return (words[0].charAt(0) + words[1].charAt(0)).toUpperCase();
        }
    };

    return imgError || !src ? (
        <div className={`avatar-fallback ${className}`} onClick={onClick}>
            {getInitials(fallbackText)}
        </div>
    ) : (
        <img
            src={src}
            alt={fallbackText}
            className={className}
            onError={() => setImgError(true)}
            onClick={onClick}
        />
    );
};

export default AvatarConFallback;
