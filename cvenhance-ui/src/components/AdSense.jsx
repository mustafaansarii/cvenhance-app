import React, { useEffect } from 'react';

const AdSense = ({ adSlot, adFormat = 'auto', fullWidthResponsive = true, style, className = '' }) => {
  useEffect(() => {
    try {
      if (window) {
        (window.adsbygoogle = window.adsbygoogle || []).push({});
      }
    } catch (err) {
      console.error('AdSense push error:', err);
    }
  }, []);

  return (
    <div className={`ad-container ${className}`} style={{ overflow: 'hidden', textAlign: 'center', ...style }}>
      <ins
        className="adsbygoogle"
        style={{ display: 'block' }}
        data-ad-client="ca-pub-1709597039590800"
        data-ad-slot={adSlot}
        data-ad-format={adFormat}
        data-full-width-responsive={fullWidthResponsive ? 'true' : 'false'}
      />
    </div>
  );
};

export default AdSense;

