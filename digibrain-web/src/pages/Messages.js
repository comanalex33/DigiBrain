import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';

import '../App.css'

import apiService from '../services/ApiService';

function Messages() {

    const [title, setTitle] = useState('')
    const [message, setMessage] = useState('')
    const [targets, setTargets] = useState([])

    const token = sessionStorage.getItem('token')
    const config = { headers: { 'Authorization': 'Bearer ' + token } }
    const { i18n, t } = useTranslation()

    const handleTitleChanged = event => {
        setTitle(event.target.value)
    }

    const handleMessageChanged = event => {
        setMessage(event.target.value)
    }

    const handleTargetChanged = (to) => {
        if (targets.includes(to)) {
            const newTargets = targets.filter(item => item !== to)
            setTargets(newTargets)
        } else {
            const newTargets = [...targets]
            newTargets.push(to)
            setTargets(newTargets)
        }
    }

    const sendMessage = () => {
        if (title === '' || message === '') {
            alert(t("title_and_message_not_empty"))
            return
        }

        if (targets.length === 0) {
            alert(t("empty_destination"))
            return
        }

        if (targets.length === 3) {
            apiService.notify(title, message, 'all', config)
                .then(response => {
                    alert(t("all_users_notified"))
                })
                .catch(error => {
                    console.log(error)
                })
        } else {
            targets.forEach(to => {
                apiService.notify(title, message, to, config)
                    .then(response => {
                        switch (to) {
                            case 'student':
                                alert(`${t("students")} ${t("notified")}`)
                                break
                            case 'teacher':
                                alert(`${t("teachers")} ${t("notified")}`)
                                break
                            case 'admin':
                                alert(`${t("admins")} ${t("notified")}`)
                                break
                        }
                    })
                    .catch(error => {
                        console.log(error)
                    })
            });


        }
    }

    return (
        <div className='Page-fit d-flex justify-content-center align-items-center'>
            <div className='w-50 h-50 message-box border border-dark p-3'>
                <div className='d-flex gap-4'>
                    <label className='fs-4'>{t("title")}</label>
                    <input type='text' className='w-25' value={title} onChange={handleTitleChanged} />
                </div>
                <label className='fs-4'>{t("message")}</label>
                <textarea className='w-100 h-50' value={message} onChange={handleMessageChanged} />
                <div className='d-flex align-items-center'>
                    <label className='fs-4'>{t("to")}</label>
                    <div className='w-100 d-flex justify-content-around btn-toolbar'>
                        <div className={'m-1 p-2 button-round w-25 d-flex justify-content-center align-items-center ' + ((targets.includes('student') === true) ? 'active-button' : 'inactive-button')}
                            onClick={() => handleTargetChanged('student')}>{t("students")}</div>
                        <div className={'m-1 p-2 button-round w-25 d-flex justify-content-center align-items-center ' + ((targets.includes('teacher') === true) ? 'active-button' : 'inactive-button')}
                            onClick={() => handleTargetChanged('teacher')}>{t("teachers")}</div>
                        <div className={'m-1 p-2 button-round w-25 d-flex justify-content-center align-items-center ' + ((targets.includes('admin') === true) ? 'active-button' : 'inactive-button')}
                            onClick={() => handleTargetChanged('admin')}>{t("admins")}</div>
                    </div>
                </div>
                <div className='float-end w-100 h-25 d-flex justify-content-center align-items-center'>
                    <button className='btn btn-primary w-25 fs-5' onClick={sendMessage}>{t("send")}</button>
                </div>
            </div>
        </div>
    )
}

export default Messages;