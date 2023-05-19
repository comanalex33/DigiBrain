import React from 'react'
import '../css/Popup.css'

function WidePopup(props) {

    return (props.trigger) ? (
        <div className='popup'>
            <div className='wide-popup-inner'>
                {props.children}
            </div>
        </div>
    ) : "";
}

export default WidePopup