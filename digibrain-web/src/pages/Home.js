import React from 'react';

import '../App.css'

function Home() {
    return (
        <div className='Page-fit'>
            <div className='d-flex h-100'>
                <div className="flex-grow-1 d-flex justify-content-center align-items-center" style={{width: 40 + '%'}}>
                    <img src='/image.webp' alt='Image' className='Home-image'/>
                </div>
                <div className="flex-grow-1" style={{width: 60 + '%'}}>
                    <div className='Home-title'>Administration Page</div>
                    <div className='Home-content'>
                        Content
                    </div>
                    <div className='Home-footer'><i>Explore, Learn, Succeed: Your Educational Journey Starts Here</i></div>
                </div>
            </div>
        </div>
    );
};

export default Home;
