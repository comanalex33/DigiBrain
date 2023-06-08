import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import {
    Nav,
    NavLink,
    NavMenu,
    NavBtn,
    NavBtnLink
} from './NavbarElements';

import { useNavigate } from "react-router-dom";

import apiService from '../services/ApiService';

const Navbar = () => {

    const [languages, setLanguages] = useState([])

    let isUserLoggedIn = sessionStorage.getItem('token');

    const navigate = useNavigate();
    const { i18n, t } = useTranslation()

    useEffect(() => {
        apiService.getLanguages()
            .then(response => {
                setLanguages(response.data)
            })
            .catch(error => {
                console.log(error)
            })
    }, [])

    const handleLogInButton = event => {
        navigate('/login');
    }

    const handleLogOutButton = event => {
        navigate('');
        sessionStorage.clear();
    }

    const onChangeLang = event => {
        const lang_code = event.target.value
        setLanguageId(lang_code)
        i18n.changeLanguage(lang_code)
    }

    const setLanguageId = (code) => {
        languages.forEach(language => {
            if (language.code == code) {
                sessionStorage.setItem('languageId', language.id)
                return
            }
        });
    }

    return (
        <div className='d-flex'>
            <Nav style={{ width: 90 + '%' }}>
                <div className='h-100 d-flex align-items-center'>
                    <img src='/logo.svg' alt='' style={{ height: 80 + '%' }} />
                </div>
                <NavLink to="" className='fs-3'>
                    Digi Brain
                </NavLink>
                <NavMenu>
                    {(isUserLoggedIn !== null) &&
                        <NavLink to="/learn" >
                            {t("learn_tab")}
                        </NavLink>
                    }

                    {(isUserLoggedIn !== null) &&
                        <NavLink to="/quiz" >
                            {t("quiz_tab")}
                        </NavLink>
                    }

                    {(isUserLoggedIn !== null) &&
                        <NavLink to="/learn-path" >
                            {t("learn_path_tab")}
                        </NavLink>
                    }
                </NavMenu>

                {(isUserLoggedIn === null) ?
                    <NavBtn>
                        <NavBtnLink onClick={handleLogInButton}>{t("sign_in_button")}</NavBtnLink>

                    </NavBtn> :
                    <NavBtn>
                        <NavBtnLink onClick={handleLogOutButton}>{t("sign_out_button")}</NavBtnLink>
                    </NavBtn>
                }
            </Nav>
            <div className=' w-auto d-flex align-items-center'>
                <select className='h-auto form-select' defaultValue={i18n.language} onChange={onChangeLang}>
                    {languages.map((language, _) => (
                        <option key={language.code} value={language.code}>
                            {language.name}
                        </option>
                    ))}
                </select>
            </div>
        </div>
    );
};

export default Navbar;