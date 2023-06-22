import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';

import '../App.css'

import apiService from '../services/ApiService';
import Popup from '../components/Popup';

function Requests() {

    const [confirmDeletePopup, setConfirmDeletePopup] = useState(false)

    const [role, setRole] = useState('teacher')
    const [users, setUsers] = useState([])

    const [accept, setAccept] = useState(false)
    const [selectedUsername, setSelectedUsername] = useState('')

    const username = sessionStorage.getItem('username')
    const token = sessionStorage.getItem('token')
    const config = { headers: { 'Authorization': 'Bearer ' + token } }
    const { i18n, t } = useTranslation()

    let teacherRequests = users.filter(item => item.roleRequest === 'teacher')
    let adminRequests = users.filter(item => item.roleRequest === 'admin')

    useEffect(() => {
        getRoleRequests()
    }, [])

    const getRoleRequests = () => {
        apiService.getRoleRequests(config)
            .then(response => {
                console.log(response.data)
                setUsers(response.data)
            })
            .catch(error => {
                console.log(error)
            })
    }

    const changeRole = () => {
        apiService.changeUserRole(selectedUsername, accept, config)
            .then(response => {
                let status = (accept === true) ? t("accepted") : t("rejected")
                apiService.notify(`${t("role_change")} ${status}`, `${t("notify_message_1")} ${role} ${t("notify_message_2")} ${status} ${t("by")} ${username}`, `user-${selectedUsername}`, config)
                    .then(response => {
                        console.log("User notified")
                    })
                    .catch(error => {
                        console.log(error)
                    })
                setConfirmDeletePopup(false)
                getRoleRequests()
            })
            .catch(error => {
                console.log(error)
            })
    }

    return (
        <div className='Page-fit p-3 d-flex flex-column'>
            <div className='fs-2 w-100 text-center'>{t("role_change")}</div>
            <div className='d-flex w-100 p-3 gap-5'>
                <div className={'w-50 fs-4 d-flex justify-content-center align-items-center ' + ((role == 'teacher') ? 'active-button' : 'inactive-button')}
                    onClick={() => setRole('teacher')}>{t("teacher")}</div>
                <div className={'w-50 fs-4 d-flex justify-content-center align-items-center ' + ((role == 'admin') ? 'active-button' : 'inactive-button')}
                    onClick={() => setRole('admin')}>{t("admin")}</div>
            </div>
            <div className='flex-grow-1 w-100 d-flex'>
                {(role === 'teacher') ?
                    <div className='flex-grow-1 d-flex'>
                        {(teacherRequests.length === 0) ?
                            <div className='flex-grow-1 d-flex align-items-center justify-content-center fs-4 text-danger'>{t("no_teacher_request")}</div>
                            :
                            <div className='w-100 d-flex align-items-center mt-3 flex-column gap-3'>
                                {teacherRequests.map((user, _) => (
                                    <div key={user.id} className='card w-75 p-3 d-flex flex-row justify-content-between align-items-center'>
                                        <div className='d-flex flex-column'>
                                            <b>{user.userName}</b>
                                            <div style={{ marginLeft: 20 + 'px' }}>
                                                <label className='pl-3'>{t("email")}: {user.email}</label>
                                            </div>
                                        </div>
                                        <div>
                                            <i className='fa fa-check icon m-2' onClick={() => {
                                                setSelectedUsername(user.userName)
                                                setAccept(true)
                                                setConfirmDeletePopup(true)
                                            }} />
                                            <i className='fa fa-times icon m-2' onClick={() => {
                                                setSelectedUsername(user.userName)
                                                setAccept(false)
                                                setConfirmDeletePopup(true)
                                            }} />
                                        </div>
                                    </div>
                                ))}
                            </div>
                        }
                    </div>
                    :
                    <div className='flex-grow-1 d-flex'>
                        {(adminRequests.length === 0) ?
                            <div className='flex-grow-1 d-flex align-items-center justify-content-center fs-4 text-danger'>{t("no_admin_request")}</div>
                            :
                            <div className='w-100 d-flex align-items-center mt-3 flex-column gap-3'>
                                {adminRequests.map((user, _) => (
                                    <div key={user.id} className='card w-75 p-3 d-flex flex-row justify-content-between align-items-center'>
                                        <div className='d-flex flex-column'>
                                            <b>{user.userName}</b>
                                            <div style={{ marginLeft: 20 + 'px' }}>
                                                <label className='pl-3'>{t("email")}: {user.email}</label>
                                            </div>
                                        </div>
                                        <div>
                                            <i className='fa fa-check icon m-2' onClick={() => {
                                                setSelectedUsername(user.userName)
                                                setAccept(true)
                                                setConfirmDeletePopup(true)
                                            }} />
                                            <i className='fa fa-times icon m-2' onClick={() => {
                                                setSelectedUsername(user.userName)
                                                setAccept(false)
                                                setConfirmDeletePopup(true)
                                            }} />
                                        </div>
                                    </div>
                                ))}
                            </div>
                        }
                    </div>
                }
            </div>
            <Popup trigger={confirmDeletePopup}>
                <div className='d-flex justify-content-center'>
                    {(role === 'teacher') ?
                        <>
                            {(accept === true) ?
                                <p>{t("teacher_approve_check")} {selectedUsername}?</p>
                                :
                                <p>{t("teacher_decline_check")} {selectedUsername}?</p>
                            }
                        </>
                        :
                        <>
                            {(accept === true) ?
                                <p>{t("admin_approve_check")} {selectedUsername}?</p>
                                :
                                <p>{t("admin_decline_check")} {selectedUsername}?</p>
                            }
                        </>
                    }
                </div>
                <div className='w-100 d-flex justify-content-around'>
                    <button className='btn btn-primary' onClick={changeRole}>{t("yes")}</button>
                    <button className='btn btn-danger' onClick={() => setConfirmDeletePopup(false)}>{t("no")}</button>
                </div>
            </Popup>
        </div>
    )
}

export default Requests;