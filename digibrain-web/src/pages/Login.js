import React, { useState } from 'react';
import { useNavigate } from "react-router-dom";
import jwt from 'jwt-decode'
import { useTranslation } from 'react-i18next';
import {
    MDBBtn,
    MDBContainer,
    MDBCard,
    MDBCardBody,
    MDBInput,
    MDBIcon,
    MDBRow,
    MDBCol,
    MDBCheckbox,
    MDBTabs,
    MDBTabsItem,
    MDBTabsLink,
    MDBTabsContent,
    MDBTabsPane,
}
    from 'mdb-react-ui-kit';

import apiService from '../services/ApiService';

function App() {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [justifyActive, setJustifyActive] = useState('tab1');
    
    const navigate = useNavigate()
    const { i18n, t } = useTranslation()

    const handleJustifyClick = (value) => {
        if (value === justifyActive) {
            return;
        }

        setJustifyActive(value);
    };

    const handleUsernameChange = event => {
        setUsername(event.target.value)
    }

    const handlePasswordChange = event => {
        setPassword(event.target.value)
    }

    const handleLogin = () => {
        if (username === '') {
            alert("Username field is empty!")
            return
        }

        if (password === '') {
            alert("Password field is empty!")
            return
        }

        apiService.login(username, password)
            .then(response => {
                const token = response.data.token;
                console.log(token);
                const decode = jwt(token);
                console.log(decode.roles);
                sessionStorage.setItem('token', token);
                sessionStorage.setItem('username', decode.name);
                sessionStorage.setItem('languageId', 1)
                sessionStorage.setItem('role', decode.roles);
                if (decode.roles === 'admin' || decode.roles === 'teacher') {
                    navigate("/")
                }
            })
            .catch(error => {
                console.log(error)
                alert(error.response.data.message)
            })
    }

    return (
        <div className='Page-fit'>
        <MDBContainer fluid className=' w-50'>

            <MDBTabs pills justify className='mb-3 d-flex flex-row justify-content-between'>
                <MDBTabsItem>
                    <MDBTabsLink onClick={() => handleJustifyClick('tab1')} active={justifyActive === 'tab1'}>
                        {t("login")}
                    </MDBTabsLink>
                </MDBTabsItem>
                <MDBTabsItem>
                    <MDBTabsLink onClick={() => handleJustifyClick('tab2')} active={justifyActive === 'tab2'}>
                        {t("register")}
                    </MDBTabsLink>
                </MDBTabsItem>
            </MDBTabs>

            <MDBTabsContent>

                <MDBTabsPane show={justifyActive === 'tab1'}>
                    <MDBRow className='g-0 align-items-center d-flex'>
                        <MDBCol col='6'>

                            <MDBCard className='my-5 cascading-right' style={{ background: 'hsla(0, 0%, 100%, 0.55)', backdropFilter: 'blur(30px)' }}>
                                <MDBCardBody className='p-5 shadow-5 text-center'>

                                    <h2 className="fw-bold mb-5">{t("sign_in")}</h2>

                                    <MDBInput wrapperClass='mb-4' label={t("username")} id='form3' type='username' onChange={handleUsernameChange} />
                                    <MDBInput wrapperClass='mb-4' label={t("password")} id='form4' type='password' onChange={handlePasswordChange} />

                                    <MDBBtn className='w-100 mb-4' size='md' onClick={handleLogin}>{t("login")}</MDBBtn>

                                </MDBCardBody>
                            </MDBCard>
                        </MDBCol>
                    </MDBRow>
                </MDBTabsPane>

                <MDBTabsPane show={justifyActive === 'tab2'}>
                    <MDBRow className='g-0 align-items-center d-flex'>
                        <MDBCol col='6'>

                            <MDBCard className='my-5 cascading-right' style={{ background: 'hsla(0, 0%, 100%, 0.55)', backdropFilter: 'blur(30px)' }}>
                                <MDBCardBody className='p-5 shadow-5 text-center'>

                                    <h2 className="fw-bold mb-5">{t("sign_up")}</h2>

                                    <MDBInput wrapperClass='mb-4' label={t("username")} id='form3' type='username' />
                                    <MDBInput wrapperClass='mb-4' label={t("email")} id='form3' type='email' />
                                    <MDBInput wrapperClass='mb-4' label={t("password")} id='form4' type='password' />
                                    <MDBInput wrapperClass='mb-4' label={t("password")} id='form4' type='password' />

                                    <MDBBtn className='w-100 mb-4' size='md'>{t("register")}</MDBBtn>

                                </MDBCardBody>
                            </MDBCard>
                        </MDBCol>
                    </MDBRow>
                </MDBTabsPane>

            </MDBTabsContent>

        </MDBContainer>
        </div>
    );
}

export default App;