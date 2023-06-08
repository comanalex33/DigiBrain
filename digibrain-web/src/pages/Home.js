import React from 'react';
import { useTranslation } from 'react-i18next';

import '../App.css'

function Home() {
    const { i18n, t } = useTranslation()

    return (
        <div className='Page-fit'>
            <div className='d-flex h-100'>
                <div className="flex-grow-1 d-flex justify-content-center align-items-center" style={{ width: 60 + '%' }}>
                    <img src='/boy-studying.jpg' alt='Image' className='Home-image' style={{ width: 90 + '%', height: 'auto' }} />
                </div>
                <div className="flex-grow-1" style={{ width: 40 + '%' }}>
                    <div className='d-flex flex-column justify-content-center' style={{ height: 60 + '%' }}>
                        <div className='Home-title'>{t("home_page_title")}</div>
                        <div className='Home-content'>{t("home_page_description")}</div>
                    </div>
                    <div style={{ height: 40 + '%' }} className='Home-footer'><i>{t("home_page_motto")}</i></div>
                </div>
            </div>
        </div>
    );
};

export default Home;
