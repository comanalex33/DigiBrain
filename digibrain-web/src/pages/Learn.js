import React, { useState } from 'react';
import Form from 'react-bootstrap/Form';
import { ListGroup, Tab } from 'react-bootstrap';

import '../App.css'
import Popup from '../components/Popup';
import WidePopup from '../components/WidePopup';
import apiService from '../services/ApiService';

function Learn() {

    const [popup, setPopup] = useState(false)
    const [editLessonPopup, setEditLessonPopup] = useState(false)

    const [classNumber, setClassNumber] = useState(0)
    const [university, setUniversity] = useState(false)

    const [subjects, setSubjects] = useState([])
    const [chapters, setChapters] = useState([])
    const [lessons, setLessons] = useState([])

    const [editedLessonId, setEditedLessonId] = useState(0)
    const [lessonTitle, setLessonTitle] = useState('')
    const [lessonText, setLessonText] = useState('')

    const [subjectName, setSubjectName] = useState('')
    const [addSubjectOpen, setAddSubjectOpen] = useState(false)
    const [subjectOpened, setSubjectOpened] = useState(false)

    const [chapterName, setChapterName] = useState('')
    const [addChapterOpen, setAddChapterOpen] = useState(false)

    const [selectedClassId, setSelectedClassId] = useState(0)
    const [selectedSubjectId, setSelectedSubjectId] = useState(0)
    const [selectedChapterId, setSelectedChapterId] = useState(0)

    const token = sessionStorage.getItem('token')
    const config = { headers: { 'Authorization': 'Bearer ' + token } }

    let subjectsList = []
    let chaptersList = []

    const handleButtonClick = (number, university) => {
        setClassNumber(number)
        setUniversity(university)
    }

    const handleLessonTitleChange = event => {
        setLessonTitle(event.target.value)
    }

    const handleLessonTextChange = event => {
        setLessonText(event.target.value)
    }

    const handleChapterNameChange = event => {
        setChapterName(event.target.value)
    }

    const handleSubjectNameChange = event => {
        setSubjectName(event.target.value)
    }

    const handleLessonEdit = (item) => {
        if (item === null) {
            setEditedLessonId(0)
            setEditLessonPopup(true)
            setLessonTitle('')
            setLessonText('')
        } else {
            setEditedLessonId(item.id)
            setEditLessonPopup(true)
            setLessonTitle(item.title)
            setLessonText(item.text)
        }
    }

    const renderSchoolButtons = () => {
        const numbers = [1, 2, 3, 4, 5, 6, 7, 8];

        const firstRow = numbers.slice(0, 4);
        const secondRow = numbers.slice(4, 8);

        return (
            <div>
                <div className='d-flex w-100 justify-content-around'>
                    {firstRow.map((number) => (
                        <button
                            key={number}
                            onClick={() => handleButtonClick(number, false)}
                            className={classNumber === number && university === false ? 'Learn-selected-button' : ''}>
                            {number}
                        </button>
                    ))}
                </div>
                <br />
                <div className='d-flex w-100 justify-content-around'>
                    {secondRow.map((number) => (
                        <button
                            key={number}
                            onClick={() => handleButtonClick(number, false)}
                            className={classNumber === number && university === false ? 'Learn-selected-button' : ''}>
                            {number}
                        </button>
                    ))}
                </div>
            </div>
        );
    }

    const renderHighschoolButtons = () => {
        const numbers = [9, 10, 11, 12];

        return (
            <div>
                <div className='d-flex w-100 justify-content-around'>
                    {numbers.map((number) => (
                        <button
                            key={number}
                            onClick={() => handleButtonClick(number, false)}
                            className={classNumber === number && university === false ? 'Learn-selected-button' : ''}>
                            {number}
                        </button>
                    ))}
                </div>
            </div>
        );
    }

    const getRomanFromNumber = (number) => {
        switch (number) {
            case 1:
                return 'I'
            case 2:
                return 'II'
            case 3:
                return 'III'
            case 4:
                return 'IV'
            case 5:
                return 'V'
            case 6:
                return 'VI'
            default:
                return null
        }
    }

    const renderUniversityButtons = () => {
        const numbers = [1, 2, 3, 4, 5, 6, 7, 8];

        const firstRow = numbers.slice(0, 4);
        const secondRow = numbers.slice(4, 8);

        return (
            <div>
                <div className='d-flex w-100 justify-content-around'>
                    {firstRow.map((number) => (
                        <button
                            key={number}
                            onClick={() => handleButtonClick(number, true)}
                            className={classNumber === number && university === true ? 'Learn-selected-button' : ''}>
                            {number}
                        </button>
                    ))}
                </div>
                <br />
                <div className='d-flex w-100 justify-content-around'>
                    {secondRow.map((number) => (
                        <button
                            key={number}
                            onClick={() => handleButtonClick(number, true)}
                            className={classNumber === number && university === true ? 'Learn-selected-button' : '' +
                                (number === 7 || number === 8 ? ' invisible' : '')}>
                            {getRomanFromNumber(number)}
                        </button>
                    ))}
                </div>
            </div>
        );
    }

    const getSubjects = () => {
        setPopup(false)

        apiService.getClassByNumberAndDomain(classNumber, university, 0)
            .then(response => {
                setSelectedClassId(response.data.id)
                apiService.getSubjects(response.data.id, 2)
                    .then(response2 => {
                        setSubjects(response2.data)
                    })
                    .catch(error => {
                        console.log(error)
                    })
            })
            .catch(error => {
                console.log(error)
            })
    }

    const handleSelectChange = (event) => {
        setSelectedSubjectId(event.target.value)
    }

    const updateLesson = () => {
        if (editedLessonId === 0) {
            apiService.addLesson(lessonTitle, lessonText, selectedChapterId, config)
                .then(response => {
                    setEditLessonPopup(false)
                    handleTabSelect(selectedChapterId)
                })
                .catch(error => {
                    console.log(error)
                })
        }
    }

    const addChapter = () => {
        if (chapterName !== '') {
            let position = chapters.length - 1
            let nextChapterNumber = position < 0 ? 1 : chapters[position].number + 1
            apiService.addChapter(nextChapterNumber, chapterName, selectedSubjectId, config)
                .then(response => {
                    setChapterName('')
                    setAddChapterOpen(false)
                    openSubject()
                })
                .catch(error => {
                    console.log(error)
                })
        }
    }

    const addSubject = () => {
        if (subjectName !== '') {
            apiService.addSubject(subjectName, selectedClassId, 2, 0, config)
                .then(response => {
                    setSubjectName('')
                    setAddSubjectOpen(false)
                    getSubjects()
                })
                .catch(error => {
                    console.log(error)
                })
        }
    }

    const openSubject = () => {
        if (selectedSubjectId !== 0) {
            console.log(`Selected subject id: ${selectedSubjectId}`)
            apiService.getChapters(selectedSubjectId)
                .then(response => {
                    setChapters(response.data)
                    setSubjectOpened(true)
                })
                .catch(error => {
                    console.log(error)
                })
        }
    }

    subjectsList = [
        <option value={0} key={0}>-- Choose one subject --</option>,
        subjects.map((item, _) => (
            <option value={item.id} key={item.id}>{item.name}</option>
        ))
    ]

    chaptersList = chapters.map((item, _) => (
        <li>{item.name}</li>
    ))

    const renderChapters = () => {
        return (
            <div className="row">
                <div className="col-4">
                    <ListGroup id="list-tab">
                        {chapters.map((item, _) => (
                            <ListGroup.Item key={item.id} action onClick={() => handleTabSelect(`${item.id}`)} eventKey={item.id}>
                                {item.name}
                            </ListGroup.Item>
                        ))}
                        {(subjectOpened === true) &&
                            <div className={'d-inline d-flex align-items-center' + (addChapterOpen === false ? ' d-none' : '')}>
                                <input type='text' className='m-1 w-100' onChange={handleChapterNameChange} />
                                <i className='fa fa-check icon m-2' onClick={addChapter} />
                                <i className='fa fa-times icon m-2' onClick={() => setAddChapterOpen(false)} />
                            </div>
                        }
                        {(subjectOpened === true) &&
                            <div className='card card-add m-3' onClick={() => setAddChapterOpen(true)}>
                                <div className="card-header d-flex justify-content-center">
                                    <i className='fa fa-plus icon' />
                                </div>
                            </div>
                        }
                    </ListGroup>
                </div>
                <div className="col-8">
                    <Tab.Content>
                        {chapters.map((item, _) => (
                            <Tab.Pane key={item.id} eventKey={item.id} className={activeTab === `${item.id}` ? 'show active' : ''}>
                                {renderLessons()}
                                <div className='card card-add m-3' onClick={() => handleLessonEdit(null)}>
                                    <div className="card-header d-flex justify-content-center">
                                        <i className='fa fa-plus icon' />
                                    </div>
                                </div>
                            </Tab.Pane>
                        ))}
                    </Tab.Content>
                </div>
            </div>
        )
    }

    const renderLessons = () => {
        return (
            <div id="accordion">
                {lessons.map((item, _) => (
                    <div className="card" key={item.id}>
                        <div className="card-header">
                            <h5 className="mb-0">
                                <div className='float-start'>
                                    <button className="btn btn-link" onClick={() => handleAccordionToggle(item.title)}>
                                        {item.title}
                                    </button>
                                </div>
                                <div className='float-end'>
                                    <i className='fa fa-edit icon' onClick={() => handleLessonEdit(item)} />
                                </div>
                            </h5>
                        </div>

                        <div className={"collapse" + (activeKey === item.title ? ' show' : '')}>
                            <div className="card-body" dangerouslySetInnerHTML={{ __html: item.text }} />
                        </div>
                    </div>
                ))}
            </div>
        )
    }

    // subjectsList = subjects.map((item, _) => (
    //     <option value={item.id} key={item.id}>{item.name}</option>
    // ))

    const [activeTab, setActiveTab] = useState('home');

    const handleTabSelect = (selectedTab) => {
        console.log(selectedTab)
        setActiveTab(selectedTab);
        setSelectedChapterId(selectedTab)
        apiService.getLessons(selectedTab)
            .then(response => {
                setLessons(response.data)
                console.log(response)
            })
            .catch(error => {
                console.log(error)
            })
    };

    const [activeKey, setActiveKey] = useState('');

    const handleAccordionToggle = (selectedToggle) => {
        if (activeKey === selectedToggle)
            setActiveKey('')
        else
            setActiveKey(selectedToggle);
    };

    return (
        <div className='Page-fit'>
            <div className='d-flex'>
                <button onClick={() => setPopup(true)}>Select class</button>
                <p>Class: {classNumber}</p>
                <p>University: {university === false ? 'False' : 'True'}</p>
            </div>
            <div className='d-flex align-items-center'>
                <p>Subject</p>
                <Form.Select aria-label="Default select example" onChange={handleSelectChange}>
                    {subjectsList}
                </Form.Select>
                <div className='card card-add m-3' onClick={() => setAddSubjectOpen(true)}>
                    <div className="card-header d-flex justify-content-center">
                        <i className='fa fa-plus icon' />
                    </div>
                </div>
            </div>
            <div className={'d-inline d-flex align-items-center' + (addSubjectOpen === false ? ' d-none' : '')}>
                <input type='text' className='m-1 w-100' onChange={handleSubjectNameChange} />
                <i className='fa fa-check icon m-2' onClick={addSubject} />
                <i className='fa fa-times icon m-2' onClick={() => setAddSubjectOpen(false)} />
            </div>
            <div>
                <button onClick={openSubject}>Open</button>
                {renderChapters()}
            </div>
            <Popup trigger={popup}>
                <h2 id='title-accept-delete'>School</h2>
                {renderSchoolButtons()}
                <h2 id='title-accept-delete'>Highschool</h2>
                {renderHighschoolButtons()}
                <h2 id='title-accept-delete'>University</h2>
                {renderUniversityButtons()}
                <div className='d-flex justify-content-end'>
                    <button id='button-reject' onClick={getSubjects}>Ok</button>
                </div>
            </Popup>
            <WidePopup trigger={editLessonPopup}>
                <div>
                    <label>Title: </label>
                    <input type='text' value={lessonTitle} onChange={handleLessonTitleChange} />
                </div>
                <div className='d-flex'>
                    <textarea className='w-50' value={lessonText} onChange={handleLessonTextChange} />
                    <div className='w-50 p-3' dangerouslySetInnerHTML={{ __html: lessonText }} />
                </div>
                <div className='w-100'>
                    <button className='float-start' onClick={() => setEditLessonPopup(false)}>Cancel</button>
                    <button className='float-end' onClick={updateLesson}>Save</button>
                </div>
            </WidePopup>
        </div>
    );
};

export default Learn;
