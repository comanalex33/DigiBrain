import React, { useState, useEffect } from 'react';
import Form from 'react-bootstrap/Form';
import { ListGroup, Tab } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';

import '../App.css'
import '../css/Learn.css'

import Popup from '../components/Popup';
import WidePopup from '../components/WidePopup';
import ClassSelector from '../components/ClassSelector';

import apiService from '../services/ApiService';

const CHAPTER = 0
const LESSON = 1

function Learn() {

    const [popup, setPopup] = useState(false)
    const [editLessonPopup, setEditLessonPopup] = useState(false)
    const [confirmDeletePopup, setConfirmDeletePopup] = useState(false)

    const [classNumber, setClassNumber] = useState(0)
    const [university, setUniversity] = useState(false)
    const [languageId, setLanguageId] = useState(0)

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
    const [editChapter, setEditChapter] = useState(false)
    const [editedChapterId, setEditedChapterId] = useState(0)

    const [selectedClassId, setSelectedClassId] = useState(0)
    const [selectedSubjectName, setSelectedSubjectName] = useState('')
    const [selectedSubjectId, setSelectedSubjectId] = useState(0)
    const [selectedChapterId, setSelectedChapterId] = useState(0)

    const [deletedElement, setDeletedElement] = useState(-1)
    const [deletedElementId, setDeletedElementId] = useState(-1)

    const [searchFilterActive, setSearchFilterActive] = useState(true);

    const token = sessionStorage.getItem('token')
    const config = { headers: { 'Authorization': 'Bearer ' + token } }
    const { i18n, t } = useTranslation()

    let subjectsList = []

    useEffect(() => {
        const handleLanguageChange = () => {
            // Call your method or function here
            const id = sessionStorage.getItem('languageId')
            setLanguageId(id)

            if (classNumber !== 0) {
                setSubjectOpened(false)
                setSelectedSubjectName('')
                setSelectedSubjectId(0)
                getSubjects(id)
            }
        };

        const id = sessionStorage.getItem('languageId')
        setLanguageId(id)

        i18n.on('languageChanged', handleLanguageChange);

        return () => {
            i18n.off('languageChanged', handleLanguageChange);
        };
    }, [i18n, classNumber]);

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

    const handleSearchFilterChange = () => {
        setSearchFilterActive(!searchFilterActive)
    };

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

    const getSubjects = (langId) => {
        setPopup(false)

        var usedLanguageId
        if (langId !== 0)
            usedLanguageId = langId
        else
            usedLanguageId = languageId

        apiService.getClassByNumberAndDomain(classNumber, university, 0)
            .then(response => {
                setSelectedClassId(response.data.id)
                apiService.getSubjects(response.data.id, (usedLanguageId === null) ? 1 : usedLanguageId)
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
        let subjectFound = false
        subjects.forEach(subject => {
            if (subject.id == event.target.value) {
                setSelectedSubjectName(subject.name)
                subjectFound = true
            }
        });
        if (subjectFound === false) {
            setSelectedSubjectName('')
        }
    }

    const saveLesson = () => {
        if (lessonTitle !== null && lessonText !== null) {
            if (editedLessonId === 0) {
                apiService.addLesson(lessonTitle, lessonText, selectedChapterId, config)
                    .then(response => {
                        setEditLessonPopup(false)
                        handleTabSelect(selectedChapterId)
                    })
                    .catch(error => {
                        console.log(error)
                    })
            } else {
                apiService.updateLesson(editedLessonId, lessonTitle, lessonText, selectedChapterId, config)
                    .then(response => {
                        setEditLessonPopup(false)
                        handleTabSelect(selectedChapterId)
                    })
                    .catch(error => {
                        console.log(error)
                    })
            }
        }
    }

    const deleteLesson = (id) => {
        apiService.deleteLesson(id, config)
            .then(response => {
                setLessonTitle('')
                setConfirmDeletePopup(false)
                handleTabSelect(selectedChapterId)
            })
            .catch(error => {
                console.log(error)
            })
    }

    const saveChapter = (chapter = null) => {
        if (editChapter === false) {
            console.log(chapterName)
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
        } else {
            if (chapterName !== '') {
                apiService.updateChapter(chapter.id, chapter.number, chapterName, chapter.subjectId, config)
                    .then(response => {
                        setChapterName('')
                        setEditChapter(false)
                        setEditedChapterId(0)
                        openSubject()
                    })
                    .catch(error => {
                        console.log(error)
                    })
            }
        }
    }

    const deleteChapter = (id) => {
        apiService.deleteChapter(id, config)
            .then(response => {
                setChapterName('')
                setConfirmDeletePopup(false)
                openSubject()
            })
            .catch(error => {
                console.log(error)
            })
    }

    const addSubject = () => {
        if (subjectName !== '') {
            apiService.addSubject(subjectName, selectedClassId, (languageId === null) ? 1 : languageId, 0, config)
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
        <option value={0} key={0}>-- {t("choose_subject")} --</option>,
        subjects.map((item, _) => (
            <option value={item.id} key={item.id}>{item.name}</option>
        ))
    ]

    const renderChapters = () => {
        return (
            <div className="row mt-3 overflow-auto scrollable">
                <div className="col-4">
                    <ListGroup id="list-tab">
                        <>
                            {chapters.slice().sort((a, b) => a.number - b.number).map((item, _) => (
                                <ListGroup.Item key={item.id} action onClick={() => handleTabSelect(`${item.id}`)} eventKey={item.id}>
                                    {/* {item.name} */}
                                    <div className='d-flex justify-content-between p-1'>
                                        {(editChapter === true && editedChapterId == item.id) ?
                                            <input type='text' className='m-1 w-50' value={chapterName} onChange={handleChapterNameChange} />
                                            :
                                            <label>{item.name}</label>
                                        }
                                        <div className='d-flex gap-2'>
                                            {(editChapter === true && editedChapterId == item.id) ?
                                                <>
                                                    < i className='fa fa-check icon d-flex align-items-center' onClick={() => {
                                                        saveChapter(item)
                                                    }} />
                                                    < i className='fa fa-times icon d-flex align-items-center' onClick={() => {
                                                        setEditChapter(false)
                                                        setEditedChapterId(0)
                                                        setChapterName('')
                                                    }} />
                                                </>
                                                :
                                                <i className='fa-solid fa-edit icon d-flex align-items-center' onClick={() => {
                                                    setEditChapter(true)
                                                    setEditedChapterId(item.id)
                                                    setChapterName(item.name)
                                                }} />
                                            }

                                            {(editChapter === false || editedChapterId != item.id) &&
                                                <i className='fa fa-trash icon d-flex align-items-center' onClick={() => {
                                                    setDeletedElement(CHAPTER)
                                                    setChapterName(item.name)
                                                    setDeletedElementId(item.id)
                                                    setConfirmDeletePopup(true)
                                                }} />
                                            }
                                        </div>
                                    </div>
                                </ListGroup.Item>
                            ))}
                            {(subjectOpened === true) &&
                                <div className={'d-inline d-flex align-items-center' + (addChapterOpen === false ? ' d-none' : '')}>
                                    <input type='text' className='m-1 w-100' value={chapterName} onChange={handleChapterNameChange} />
                                    <i className='fa fa-check icon m-2' onClick={saveChapter} />
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
                        </>
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
                            <h5 className="d-flex justify-content-between">
                                <div>
                                    <button className="btn btn-link" onClick={() => handleAccordionToggle(item.title)}>
                                        {item.title}
                                    </button>
                                </div>
                                <div className='d-flex gap-2'>
                                    <i className='fa fa-edit icon d-flex align-items-center' onClick={() => handleLessonEdit(item)} />
                                    <i className='fa fa-trash icon d-flex align-items-center' onClick={() => {
                                        setDeletedElement(LESSON)
                                        setLessonTitle(item.title)
                                        setDeletedElementId(item.id)
                                        setConfirmDeletePopup(true)
                                    }} />
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

    const handleKeyDown = (e) => {
        if (e.key === 'Tab') {
            e.preventDefault();

            const { selectionStart, selectionEnd, value } = e.target;

            // Add four spaces at the current cursor position
            const modifiedValue = value.substring(0, selectionStart) + '    ' + value.substring(selectionEnd);
            const modifiedCursorPosition = selectionStart + 4;

            e.target.value = modifiedValue;
            e.target.setSelectionRange(modifiedCursorPosition, modifiedCursorPosition);
        }
    };

    return (
        <div className='Page-fit p-3 d-flex flex-column'>

            <div className='search-filter'>
                <div className="card-header d-flex w-100">
                    <h5 className="mb-0 w-100">
                        <div className='float-start d-flex align-items-center justify-content-between w-100'>
                            <div className="btn btn-link fs-5" onClick={handleSearchFilterChange}>
                                {t("search_filter")}
                            </div>
                            <div>
                                <i className={"fa-solid icon" + (searchFilterActive === true ? ' fa-arrow-up' : ' fa-arrow-down')} onClick={handleSearchFilterChange} />
                            </div>
                        </div>
                    </h5>
                </div>
                <div className={"collapse" + (searchFilterActive === true ? ' show' : '')}>
                    {/* Choose class */}
                    <div className='border-bottom border-dark p-1 d-flex align-items-end justify-content-between'>
                        <div className='fs-4 fw-bold'>{t("select_class")}</div>
                        <button onClick={() => setPopup(true)} className='btn btn-primary'>{t("choose_class")}</button>
                    </div>
                    {(classNumber !== 0) &&
                        <div className='d-flex'>
                            <div className="class-card">{t("selected_class")}: {classNumber}</div>
                            {(university === true) &&
                                <div className="university-card">{t("at_university")}</div>
                            }
                        </div>
                    }
                    {(classNumber === 0) &&
                        <div className='text-center text-danger fs-5'>{t("no_class_selected")}</div>
                    }

                    {/* Choose subject */}
                    <div className='border-bottom border-dark'>
                        <div className='p-1 d-flex align-items-end justify-content-between'>
                            <div className='fs-4 fw-bold'>{t("select_subject")}</div>
                            <div className='d-flex w-50'>
                                <Form.Select className='w-100' aria-label="Default select example" onChange={handleSelectChange}>
                                    {subjectsList}
                                </Form.Select>
                                <div className='card card-add ms-3' onClick={() => setAddSubjectOpen(true)}>
                                    <div className="card-header d-flex justify-content-center">
                                        <i className='fa fa-plus icon' />
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className={'d-inline d-flex align-items-center' + (addSubjectOpen === false ? ' d-none' : '')}>
                            <input type='text' className='m-1 w-100' onChange={handleSubjectNameChange} />
                            <i className='fa fa-check icon m-2' onClick={addSubject} />
                            <i className='fa fa-times icon m-2' onClick={() => setAddSubjectOpen(false)} />
                        </div>
                    </div>
                    {(selectedSubjectName !== '') &&
                        <>
                            <div className="class-card">{t("selected_subject")}: {selectedSubjectName}</div>
                        </>
                    }
                    {(selectedSubjectName === '') &&
                        <div className='text-center text-danger fs-5'>{t("no_subject_selected")}</div>
                    }

                    {/* Open sybject */}
                    <div className='mt-3'>
                        <div className='d-flex justify-content-center'>
                            <button onClick={openSubject} className='btn btn-primary w-25 fs-5'>{t("open")}</button>
                        </div>
                    </div>
                    <br />
                </div>
            </div>
            {(subjectOpened === false) ?
                <div className='flex-grow-1 d-flex align-items-center justify-content-center fs-4 text-danger'>{t("no_subject_loaded")}</div>
                :
                <>
                    {(subjectOpened === true) &&
                        <div className='fs-4 fw-bold'>{t("subject_content")}</div>
                    }
                    {renderChapters()}
                </>
            }
            <Popup trigger={popup}>
                <ClassSelector classNumber={classNumber} university={university} handleClassSelected={handleButtonClick} translator={t} />
                <div className='d-flex justify-content-end'>
                    <button className='btn btn-primary' onClick={() => getSubjects(0)}>Ok</button>
                </div>
            </Popup>
            <WidePopup trigger={editLessonPopup}>
                <div className='d-flex justify-content-center'>
                    <label className='fs-5'>{t("title")}</label>
                    <input type='text' className='lesson-title-box' value={lessonTitle} onChange={handleLessonTitleChange} />
                </div>
                <div className='d-flex'>
                    <textarea className='w-50' value={lessonText} onKeyDown={handleKeyDown} onChange={handleLessonTextChange} />
                    <div className='w-50 p-3' dangerouslySetInnerHTML={{ __html: lessonText }} />
                </div>
                <div className='w-100 mt-3'>
                    <button className='float-start btn btn-danger' onClick={() => setEditLessonPopup(false)}>{t("cancel")}</button>
                    <button className='float-end btn btn-primary' onClick={saveLesson}>{t("save")}</button>
                </div>
            </WidePopup>
            <Popup trigger={confirmDeletePopup}>
                <div className='d-flex justify-content-center'>
                    {(() => {
                        switch (deletedElement) {
                            case CHAPTER:
                                return <p className='fs-4'>{t("delete_chapter_check")}</p>
                            case LESSON:
                                return <p className='fs-4'>{t("delete_lesson_check")}</p>
                            default:
                                return
                        }
                    })()}
                </div>
                <div className='d-flex gap-3'>
                    {(() => {
                        switch (deletedElement) {
                            case CHAPTER:
                                return <label>{t("chapter")}:  {chapterName}</label>
                            case LESSON:
                                return <label>{t("lesson")}:  {lessonTitle}</label>
                            default:
                                return
                        }
                    })()}
                </div>
                <br />
                <div className='w-100 d-flex justify-content-around'>
                    <button className='btn btn-primary' onClick={() => {
                        switch (deletedElement) {
                            case CHAPTER:
                                deleteChapter(deletedElementId)
                                break
                            case LESSON:
                                deleteLesson(deletedElementId)
                                break
                        }
                    }}>{t("yes")}</button>
                    <button className='btn btn-danger' onClick={() => setConfirmDeletePopup(false)}>{t("no")}</button>
                </div>
            </Popup>
        </div>
    );
};

export default Learn;
