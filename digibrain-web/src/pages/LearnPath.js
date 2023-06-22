import React, { useState, useEffect } from 'react';
import Form from 'react-bootstrap/Form';
import { ListGroup, Tab } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';

import '../App.css'
import '../css/LearnPath.css'

import Popup from '../components/Popup';
import WidePopup from '../components/WidePopup';
import ClassSelector from '../components/ClassSelector';

import apiService from '../services/ApiService';
import utils from '../Utils';

const SECTION = 0
const LESSON = 1
const THEORY = 2
const QUIZ = 3

function LearnPath() {

    // Explorer mode
    const [popup, setPopup] = useState(false)
    const [addNewLearnPathPopup, setAddNewLearnPathPopup] = useState(false)

    const [activeKey, setActiveKey] = useState(0);

    const [classNumber, setClassNumber] = useState(0)
    const [university, setUniversity] = useState(false)
    const [languageId, setLanguageId] = useState(0)

    const [subjects, setSubjects] = useState([])

    const [newLearnPathTitle, setNewLearnPathTitle] = useState('')
    const [newLearnPathDescription, setNewLearnPathDescription] = useState('')

    const [learnPathsOpened, setLearnPathsOpened] = useState(false)
    const [learnPaths, setLearnPaths] = useState([])
    const [filteredLearnPaths, setFilteredLearnPaths] = useState([])

    const [selectedClassId, setSelectedClassId] = useState(0)
    const [selectedSubjectId, setSelectedSubjectId] = useState(0)
    const [selectedSubjectName, setSelectedSubjectName] = useState('')

    const [searchFilterActive, setSearchFilterActive] = useState(true);

    const username = sessionStorage.getItem('username')
    const token = sessionStorage.getItem('token')
    const config = { headers: { 'Authorization': 'Bearer ' + token } }
    const { i18n, t } = useTranslation()

    let subjectsList = []

    useEffect(() => {
        getLearnPaths()
    }, [])

    useEffect(() => {
        const handleLanguageChange = () => {
            // Call your method or function here
            const id = sessionStorage.getItem('languageId')
            setLanguageId(id)

            if (classNumber !== 0) {
                setLearnPathsOpened(false)
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

    const handleAccordionToggle = (selectedToggle) => {
        if (activeKey === selectedToggle)
            setActiveKey('')
        else
            setActiveKey(selectedToggle);
    };

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

    const handleSearchFilterChange = () => {
        setSearchFilterActive(!searchFilterActive)
    };

    const handleEditLearnPath = (learnPath) => {
        setEditedLearnPath(learnPath)
        setActiveSection(null)
        getLearnPathDetails(learnPath.id)
        openEditMode()
    }

    const handleNewLearnPathTitleChange = event => {
        setNewLearnPathTitle(event.target.value)
    }

    const handleNewLearnPathDescriptionChange = event => {
        setNewLearnPathDescription(event.target.value)
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

    const getLearnPaths = (filter = false) => {
        apiService.getLearnPaths()
            .then(response => {
                console.log(response.data)
                setLearnPaths(response.data)
                if (filter === true) {
                    filterLearnPaths(response.data)
                    console.log("Filtrate")
                }
            })
            .catch(error => {
                console.log(error)
            })
    }

    const filterLearnPaths = (learnPaths) => {
        console.log(selectedSubjectId)
        if (selectedSubjectId !== 0) {
            const filteredList = learnPaths.filter((item) => item.subjectId === parseInt(selectedSubjectId))
            console.log(filteredList)
            setFilteredLearnPaths(filteredList)
            setLearnPathsOpened(true)
        }
    }

    const addLearnPath = () => {
        if (newLearnPathTitle !== '' && newLearnPathDescription !== '') {
            apiService.addLearnPath(newLearnPathTitle, newLearnPathDescription, username, selectedSubjectId, null, config)
                .then(response => {
                    setAddNewLearnPathPopup(false)
                    getLearnPaths(true)
                    apiService.notify(`${t("new_learn_path_notification_title")} ${selectedSubjectName}`, `${t("new_learn_path_message_start")} ${username}, ${t("new_learn_path_message_end")}: ${newLearnPathTitle}`, `subject-${selectedSubjectId}`, config)
                        .then(response => {
                            console.log("Users notified")
                        })
                        .catch(error => {
                            console.log(error)
                        })
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

    // Edit mode
    const [editedLearnPath, setEditedLearnPath] = useState(null)
    const [detailedLearnPath, setDetailedLearnPath] = useState(null)
    const [activeSection, setActiveSection] = useState(null)
    const [activeLesson, setActiveLesson] = useState(null)

    const [selectedLearnPathTitle, setLearnPathTitle] = useState('')
    const [selectedLearnPathDescription, setLearnPathDescription] = useState('')

    const [addSectionOpen, setAddSectionOpen] = useState(false)
    const [sectionName, setSectionName] = useState('')
    const [editSection, setEditSection] = useState(false)
    const [editedSectionId, setEditedSectionId] = useState(0)

    const [addLessonOpen, setAddLessonOpen] = useState(false)
    const [lessonName, setLessonName] = useState('')
    const [editLesson, setEditLesson] = useState(false)
    const [editedLessonId, setEditedLessonId] = useState(0)

    const [editTheoryPopup, setEditTheoryPopup] = useState(false)
    const [confirmDeletePopup, setConfirmDeletePopup] = useState(false)
    const [deletedElement, setDeletedElement] = useState(-1)
    const [deletedElementId, setDeletedElementId] = useState(-1)

    const [editedTheoryId, setEditedTheoryId] = useState(0)
    const [theoryTitle, setTheoryTitle] = useState('')
    const [theoryText, setTheoryText] = useState('')

    const [chooseQuestionsPopup, setChooseQuestionsPopup] = useState(false)
    const [questions, setQuestions] = useState([])
    const [activeQuestionId, setActiveQuestionId] = useState(null)
    const [selectedQuestionIds, setSelectedQuestionIds] = useState([])
    const [newQuiz, setNewQuiz] = useState(false)

    const handleLearnPathSectionChange = (selectedSection) => {
        setActiveSection(selectedSection)
    }

    const handleQuestionChange = (id) => {
        if (id === activeQuestionId)
            setActiveQuestionId(0)
        else
            setActiveQuestionId(id)
    }

    const handleLearnPathLessonChange = (selectedLesson) => {
        if (activeLesson === selectedLesson)
            setActiveLesson(null)
        else
            setActiveLesson(selectedLesson)
    }

    const handleSectionNameChange = event => {
        setSectionName(event.target.value)
    }

    const handleLessonNameChange = event => {
        setLessonName(event.target.value)
    }

    const handleTheoryTitleChange = event => {
        setTheoryTitle(event.target.value)
    }

    const handleTheoryTextChange = event => {
        setTheoryText(event.target.value)
    }

    const handleLearnPathTitleChange = event => {
        setLearnPathTitle(event.target.value)
    }

    const handleLearnPathDescriptionChange = event => {
        setLearnPathDescription(event.target.value)
    }

    const handleTheoryEdit = (item) => {
        if (item === null) {
            setEditedTheoryId(0)
            setEditTheoryPopup(true)
            setTheoryTitle('')
            setTheoryText('')
        } else {
            setEditedTheoryId(item.id)
            setEditTheoryPopup(true)
            setTheoryTitle(item.title)
            setTheoryText(item.text)
        }
    }

    const handleQuizEdit = (questions) => {
        if (questions.length == 0) {
            setNewQuiz(true)
        } else {
            setNewQuiz(false)
        }

        setChooseQuestionsPopup(true)
        let ids = []
        questions.forEach(element => {
            ids.push(element.questionId)
        });
        setSelectedQuestionIds(ids)
    }

    const handleCheckSelectedQuestion = (event, id) => {
        if (event.target.checked === true) {
            addSelectedQuestionId(id)
        } else {
            removeSelectedQuestionId(id)
        }
    }

    const addSelectedQuestionId = (id) => {
        const list = [...selectedQuestionIds]
        list.push(id)
        setSelectedQuestionIds(list)
    }

    const removeSelectedQuestionId = (id) => {
        let list = selectedQuestionIds.filter((element) => element !== id)
        setSelectedQuestionIds(list)
    }

    const getTheoryById = (id) => {
        return activeLesson.theory.find(theory => theory.id == id) || null
    }

    const getLearnPathDetails = (id) => {
        apiService.getLearnPathDetails(id)
            .then(response => {
                setDetailedLearnPath(response.data)
                setLearnPathTitle(response.data.title)
                setLearnPathDescription(response.data.description)
            })
            .catch(error => {
                console.log(error)
            })
    }

    const getQuestions = () => {
        apiService.getQuestionsForSubject(selectedSubjectId, 0, "Any", "Any", 2)
            .then(response => {
                setQuestions(response.data)
            })
            .catch(error => {
                console.log(error)
            })
    }

    const saveLearnPath = () => {
        if (selectedLearnPathTitle !== '' && selectedLearnPathDescription !== '') {
            apiService.updateLearnPath(detailedLearnPath.id, selectedLearnPathTitle, selectedLearnPathDescription, detailedLearnPath.author, detailedLearnPath.subjectId, detailedLearnPath.imageName, config)
                .then(response => {
                    closeEditMode()
                })
                .catch(error => {
                    console.log(error)
                })
        }
    }

    const saveLearnPathSection = (section = null) => {
        if (editSection === false) {
            if (sectionName !== '') {
                let position = detailedLearnPath.sections.length - 1
                let nextSectionNumber = position < 0 ? 1 : detailedLearnPath.sections[position].number + 1
                apiService.addLearnPathSection(nextSectionNumber, sectionName, 0, detailedLearnPath.id, config)
                    .then(response => {
                        setSectionName('')
                        setAddSectionOpen(false)
                        getLearnPathDetails(detailedLearnPath.id)
                    })
                    .catch(error => {
                        console.log(error)
                    })
            }
        } else {
            if (sectionName !== '') {
                apiService.updateLearnPathSection(section.id, section.number, sectionName, section.iconId, section.pathLearnId, config)
                    .then(response => {
                        setSectionName('')
                        setEditSection(false)
                        setEditedSectionId(0)
                        getLearnPathDetails(detailedLearnPath.id)
                    })
                    .catch(error => {
                        console.log(error)
                    })
            }
        }
    }

    const deleteLearnPathSection = (id) => {
        apiService.deleteLearnPathSection(id, config)
            .then(response => {
                setSectionName('')
                setConfirmDeletePopup(false)
                getLearnPathDetails(detailedLearnPath.id)
            })
            .catch(error => {
                console.log(error)
            })
    }

    const saveLearnPathLesson = (lesson = null) => {
        if (editLesson === false) {
            if (lessonName !== '') {
                let position = activeSection.lessons.length - 1
                let nextLessonNumber = position < 0 ? 1 : activeSection.lessons[position].number + 1
                apiService.addLearnPathLesson(nextLessonNumber, lessonName, 'Test', activeSection.id, config)
                    .then(response => {
                        setLessonName('')
                        setAddLessonOpen(false)
                        getLearnPathDetails(detailedLearnPath.id)
                    })
                    .catch(error => {
                        console.log(error)
                    })
            }
        } else {
            if (lessonName !== '') {
                apiService.updateLearnPathLesson(lesson.id, lesson.number, lessonName, lesson.description, lesson.pathSectionId, config)
                    .then(response => {
                        setLessonName('')
                        setEditLesson(false)
                        setEditedLessonId(0)
                        getLearnPathDetails(detailedLearnPath.id)
                    })
                    .catch(error => {
                        console.log(error)
                    })
            }
        }
    }

    const deleteLearnPathLesson = (id) => {
        apiService.deleteLearnPathLesson(id, config)
            .then(response => {
                setLessonName('')
                setConfirmDeletePopup(false)
                getLearnPathDetails(detailedLearnPath.id)
            })
            .catch(error => {
                console.log(error)
            })
    }

    const saveQuiz = () => {
        console.log(selectedQuestionIds)
        console.log(newQuiz)

        if (newQuiz) {
            if (selectedQuestionIds.length !== 0) {
                apiService.addLearnPathQuiz(selectedQuestionIds, activeLesson.id, config)
                    .then(response => {
                        setChooseQuestionsPopup(false)
                        setSelectedQuestionIds([])
                        getLearnPathDetails(detailedLearnPath.id)
                    })
                    .catch(error => {
                        console.log(error)
                    })
            }
        } else {
            apiService.updateLearnPathQuiz(activeLesson.id, selectedQuestionIds, activeLesson.id, config)
                .then(response => {
                    setChooseQuestionsPopup(false)
                    setSelectedQuestionIds([])
                    getLearnPathDetails(detailedLearnPath.id)
                })
                .catch(error => {
                    console.log(error)
                })
        }
    }

    const deleteLearnPathQuiz = (id) => {
        apiService.deleteLearnPathTheory(id, config)
            .then(response => {
                setLessonName('')
                setConfirmDeletePopup(false)
                getLearnPathDetails(detailedLearnPath.id)
            })
            .catch(error => {
                console.log(error)
            })
    }

    const saveLearnPathTheory = () => {
        if (theoryTitle !== null && theoryText !== null) {
            if (editedTheoryId === 0) {
                let position = activeLesson.theory.length - 1
                let nextTheoryNumber = position < 0 ? 1 : activeLesson.theory[position].number + 1
                apiService.addLearnPathTheory(nextTheoryNumber, theoryTitle, theoryText, activeLesson.id, config)
                    .then(response => {
                        setTheoryTitle('')
                        setTheoryText('')
                        setEditTheoryPopup(false)
                        getLearnPathDetails(detailedLearnPath.id)
                    })
                    .catch(error => {
                        console.log(error)
                    })
            } else {
                const theory = getTheoryById(editedTheoryId)
                if (theory !== null) {
                    apiService.updateLearnPathTheory(theory.id, theory.number, theoryTitle, theoryText, activeLesson.id, config)
                        .then(response => {
                            setTheoryTitle('')
                            setTheoryText('')
                            setEditTheoryPopup(false)
                            getLearnPathDetails(detailedLearnPath.id)
                        })
                        .catch(error => {
                            console.log(error)
                        })
                }
            }
        }
    }

    const deleteLearnPathTheory = (id) => {
        apiService.deleteLearnPathTheory(id, config)
            .then(response => {
                setTheoryTitle('')
                setConfirmDeletePopup(false)
                getLearnPathDetails(detailedLearnPath.id)
            })
            .catch(error => {
                console.log(error)
            })
    }

    const renderLearnPathSections = () => {
        return (
            <div className="row mt-3">
                <div className="col-4">
                    <ListGroup id="list-tab">
                        {(detailedLearnPath !== null) &&
                            <>
                                {detailedLearnPath.sections.slice().sort((a, b) => a.number - b.number).map((section, _) => (
                                    <ListGroup.Item key={section.id} action onClick={() => handleLearnPathSectionChange(section)} eventKey={section.id}>
                                        {/* {section.title} */}
                                        <div className='d-flex justify-content-between p-1'>
                                            {(editSection === true && editedSectionId == section.id) ?
                                                <input type='text' className='m-1 w-50' value={sectionName} onChange={handleSectionNameChange} />
                                                :
                                                <label>{section.title}</label>
                                            }
                                            <div className='d-flex gap-2'>
                                                {(editSection === true && editedSectionId == section.id) ?
                                                    <>
                                                        < i className='fa fa-check icon d-flex align-items-center' onClick={() => {
                                                            saveLearnPathSection(section)
                                                        }} />
                                                        < i className='fa fa-times icon d-flex align-items-center' onClick={() => {
                                                            setEditSection(false)
                                                            setEditedSectionId(0)
                                                            setSectionName('')
                                                        }} />
                                                    </>
                                                    :
                                                    <i className='fa-solid fa-edit icon d-flex align-items-center' onClick={() => {
                                                        setEditSection(true)
                                                        setEditedSectionId(section.id)
                                                        setSectionName(section.title)
                                                    }} />
                                                }

                                                {(editSection === false || editedSectionId != section.id) &&
                                                    <i className='fa fa-trash icon d-flex align-items-center' onClick={() => {
                                                        setDeletedElement(SECTION)
                                                        setSectionName(section.title)
                                                        setDeletedElementId(section.id)
                                                        setConfirmDeletePopup(true)
                                                    }} />
                                                }
                                            </div>
                                        </div>
                                    </ListGroup.Item>
                                ))}
                                <div className={'d-inline d-flex align-items-center' + (addSectionOpen === false ? ' d-none' : '')}>
                                    <input type='text' className='m-1 w-100' value={sectionName} onChange={handleSectionNameChange} />
                                    <i className='fa fa-check icon m-2' onClick={saveLearnPathSection} />
                                    <i className='fa fa-times icon m-2' onClick={() => setAddSectionOpen(false)} />
                                </div>
                                <div className='card card-add m-3' onClick={() => setAddSectionOpen(true)}>
                                    <div className="card-header d-flex justify-content-center">
                                        <i className='fa fa-plus icon' />
                                    </div>
                                </div>
                            </>
                        }
                    </ListGroup>
                </div>
                <div className="col-8">
                    <Tab.Content>
                        {(detailedLearnPath !== null) &&
                            <>
                                {detailedLearnPath.sections.map((section, _) => (
                                    <Tab.Pane key={section.id} eventKey={section.id} className={activeSection === section ? 'show active' : ''}>
                                        {renderTheoryAndQuiz()}
                                    </Tab.Pane>
                                ))}
                            </>
                        }
                    </Tab.Content>
                </div>
            </div>
        )
    }

    const renderTheoryAndQuiz = () => {
        return (
            <div id="accordion">
                {(activeSection !== null) &&
                    <div>
                        {
                            activeSection.lessons.slice().sort((a, b) => a.number - b.number).map((lesson, _) => (
                                <div className="card" key={lesson.id}>
                                    <div className="card-header">
                                        <h5 className="mb-0">
                                            <div className='d-flex justify-content-between'>
                                                {(editLesson === true && editedLessonId == lesson.id) ?
                                                    <input type='text' className='m-1 w-25' value={lessonName} onChange={handleLessonNameChange} />
                                                    :
                                                    <button className="btn btn-link" onClick={() => handleLearnPathLessonChange(lesson)}>
                                                        {lesson.title}
                                                    </button>
                                                }
                                                <div className='d-flex gap-2'>
                                                    {(editLesson === true && editedLessonId == lesson.id) ?
                                                        <>
                                                            < i className='fa fa-check icon d-flex align-items-center' onClick={() => {
                                                                saveLearnPathLesson(lesson)
                                                            }} />
                                                            < i className='fa fa-times icon d-flex align-items-center' onClick={() => {
                                                                setEditLesson(false)
                                                                setEditedLessonId(0)
                                                                setLessonName('')
                                                            }} />
                                                        </>
                                                        :
                                                        <i className='fa fa-edit icon d-flex align-items-center' onClick={() => {
                                                            setEditLesson(true)
                                                            setEditedLessonId(lesson.id)
                                                            setLessonName(lesson.title)
                                                        }} />
                                                    }

                                                    {(editLesson === false || editedLessonId != lesson.id) &&
                                                        <i className='fa fa-trash icon d-flex align-items-center' onClick={() => {
                                                            setDeletedElement(LESSON)
                                                            setLessonName(lesson.title)
                                                            setDeletedElementId(lesson.id)
                                                            setConfirmDeletePopup(true)
                                                        }} />
                                                    }
                                                </div>
                                            </div>
                                        </h5>
                                    </div>

                                    {(activeLesson !== null) &&
                                        <div className={"collapse" + (activeLesson === lesson ? ' show' : '')}>
                                            <p className='m-2'>{t("lessons")}</p>
                                            {activeLesson.theory.slice().sort((a, b) => a.number - b.number).map((theory, position) => (
                                                <div key={position} className='card m-1'>
                                                    <div className='d-flex justify-content-between p-3'>
                                                        <label>{theory.title}</label>
                                                        <div className='d-flex gap-2'>
                                                            <i className='fa fa-edit icon d-flex align-items-center' onClick={() => handleTheoryEdit(theory)} />
                                                            <i className='fa fa-trash icon d-flex align-items-center' onClick={() => {
                                                                setDeletedElement(THEORY)
                                                                setTheoryTitle(theory.title)
                                                                setDeletedElementId(theory.id)
                                                                setConfirmDeletePopup(true)
                                                            }} />
                                                        </div>
                                                    </div>
                                                </div>
                                            ))}
                                            <div className='card card-add m-3' onClick={() => handleTheoryEdit(null)}>
                                                <div className="card-header d-flex justify-content-center">
                                                    <i className='fa fa-plus icon' />
                                                </div>
                                            </div>
                                            <p className='m-2'>{t("quiz")}</p>
                                            {activeLesson.quiz.length !== 0 ? (
                                                <div key={activeLesson.theory.length + 1} className='card m-1'>
                                                    <div className='d-flex justify-content-between p-3'>
                                                        <label>{t("quiz")}</label>
                                                        <div className='d-flex gap-2'>
                                                            <i className='fa-solid fa-edit icon d-flex align-items-center' onClick={() => handleQuizEdit(activeLesson.quiz)} />
                                                            <i className='fa fa-trash icon d-flex align-items-center' onClick={() => {
                                                                setDeletedElement(QUIZ)
                                                                setLessonName(lesson.title)
                                                                setDeletedElementId(lesson.id)
                                                                setConfirmDeletePopup(true)
                                                            }} />
                                                        </div>
                                                    </div>
                                                </div>
                                            ) : (
                                                <div className='card card-add m-3' onClick={() => handleQuizEdit([])}>
                                                    <div className="card-header d-flex justify-content-center">
                                                        <i className='fa fa-plus icon' />
                                                    </div>
                                                </div>
                                            )
                                            }
                                        </div>
                                    }
                                </div>
                            ))
                        }
                        <div className={'d-inline d-flex align-items-center' + (addLessonOpen === false ? ' d-none' : '')}>
                            <input type='text' className='m-1 w-100' value={lessonName} onChange={handleLessonNameChange} />
                            <i className='fa fa-check icon m-2' onClick={saveLearnPathLesson} />
                            <i className='fa fa-times icon m-2' onClick={() => setAddLessonOpen(false)} />
                        </div>
                        <div className='card card-add m-3' onClick={() => setAddLessonOpen(true)}>
                            <div className="card-header d-flex justify-content-center">
                                <i className='fa fa-plus icon' />
                            </div>
                        </div>
                    </div>
                }
            </div>
        )
    }

    const renderQuestions = () => {
        return (
            <div id="accordion">
                {questions.map((question, _) => (
                    <div className="card" key={question.id}>
                        <div className="card-header">
                            <h5 className="mb-0">
                                <div className='float-start d-flex align-items-center'>
                                    <Form.Check type='checkbox' checked={selectedQuestionIds.includes(question.id)} onChange={(event) => handleCheckSelectedQuestion(event, question.id)} />
                                    <button className="btn btn-link" onClick={() => handleQuestionChange(question.id)}>
                                        {question.text}
                                    </button>
                                </div>
                            </h5>
                        </div>

                        <div className={"collapse" + (activeQuestionId === question.id ? ' show' : '')}>
                            <div className="card-body">
                                <ul>
                                    {question.answers.map((answer, _) => (
                                        <li key={answer.id}>{answer.text} - {answer.correct ? t("correct") : t("wrong")} - {answer.position}</li>
                                    ))}
                                </ul>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        )
    }

    // UI composition
    const openEditMode = () => {
        const step1 = document.querySelector('#step1')
        const step2 = document.querySelector('#step2')

        step1.style.transform = 'translateX(-100%)'
        step2.style.transform = 'translateX(0)'

        getQuestions()
    }

    const closeEditMode = () => {
        const step1 = document.querySelector('#step1')
        const step2 = document.querySelector('#step2')

        step1.style.transform = 'translateX(0)'
        step2.style.transform = 'translateX(100%)'

        setDetailedLearnPath(null)
    }

    return (
        <div className='Page-fit overflow-hidden' id='container'>
            {/* Learn Path explorer mode */}
            <div className='step p-3 d-flex flex-column' id='step1'>
                <div className='search-filter'>
                    <div className="card-header d-flex w-100">
                        <h5 className="mb-0 w-100">
                            <div className='float-start d-flex align-items-center justify-content-between search-filter-bar'>
                                <div className="btn btn-link fs-5" onClick={handleSearchFilterChange}>
                                    {t("search_filter")}
                                </div>
                                <div>
                                    <i className={"fa-solid icon mr-3" + (searchFilterActive === true ? ' fa-arrow-up' : ' fa-arrow-down')} onClick={handleSearchFilterChange} />
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
                                </div>
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

                        {/* Open learn paths */}
                        <div className='d-flex justify-content-center mt-3'>
                            <button onClick={() => filterLearnPaths(learnPaths)} className='btn btn-primary w-25 fs-5'>{t("open")}</button>
                        </div>
                        <br />
                    </div>
                </div>

                {(learnPathsOpened === false) ?
                    <div className='flex-grow-1 d-flex align-items-center justify-content-center fs-4 text-danger'>{t("no_learn_paths_loaded")}</div>
                    :
                    <div className='mt-3 overflow-auto scrollable'>
                        {filteredLearnPaths.map((learnPath, _) => (
                            <div className="card" key={learnPath.id}>
                                <div className="card-header d-flex justify-content-between">
                                    <p>{learnPath.title}</p>
                                    <div className='d-flex gap-2'>
                                        <i className='fa-solid fa-edit icon' onClick={() => handleEditLearnPath(learnPath)} />
                                        <i className={"fa-solid icon" + (activeKey === learnPath.id ? ' fa-arrow-up' : ' fa-arrow-down')} onClick={() => handleAccordionToggle(learnPath.id)} />
                                    </div>
                                </div>
                                <div className={"collapse" + (activeKey === learnPath.id ? ' show' : '')}>
                                    <div className="details-card author">{t("author")}: {learnPath.author}</div>
                                    <div className="details-card description">{t("description")}: {learnPath.description}</div>
                                    <div className="details-card last-update-date">{t("last_updated")}: {utils.getFormattedTimeFromTimestamp(learnPath.date)}</div>
                                    <div className="details-card number-of-starts">{t("started")} {learnPath.started} {t("times")}</div>
                                </div>
                            </div>
                        ))}
                        <div className='card card-add m-3' onClick={() => {
                            setNewLearnPathTitle('')
                            setNewLearnPathDescription('')
                            setAddNewLearnPathPopup(true)
                        }}>
                            <div className="card-header d-flex justify-content-center">
                                <i className='fa fa-plus icon' />
                            </div>
                        </div>
                    </div>
                }
            </div>
            {/* Learn Path edit mode */}
            <div className='step p-3 overflow-auto scrollable' id='step2'>
                <div className='d-flex justify-content-between w-100 p-3'>
                    <button className='btn btn-danger' onClick={closeEditMode}>{t("cancel")}</button>
                    <button className='btn btn-primary' onClick={saveLearnPath}>{t("save")}</button>
                </div>
                {(editedLearnPath !== null) &&
                    <div className='d-flex flex-column'>
                        <div>
                            <div>
                                <label>{t("title")}:</label>
                                <input type='text' className='margin-left w-25' value={selectedLearnPathTitle} onChange={handleLearnPathTitleChange} />
                            </div>
                            <div>
                                <label>{t("author")}: {editedLearnPath.author}</label>
                            </div>
                            <div>
                                <label>{t("description")}:</label>
                                <input type='text' className='margin-left w-50' value={selectedLearnPathDescription} onChange={handleLearnPathDescriptionChange} />
                            </div>
                            <div>
                                <label>{t("last_updated")}: {utils.getFormattedTimeFromTimestamp(editedLearnPath.date)}</label>
                            </div>
                        </div>
                        {renderLearnPathSections()}
                    </div>
                }
            </div>
            <Popup trigger={popup}>
                <ClassSelector classNumber={classNumber} university={university} handleClassSelected={handleButtonClick} translator={t} />
                <div className='d-flex justify-content-end'>
                    <button className='btn btn-primary' onClick={() => getSubjects(0)}>Ok</button>
                </div>
            </Popup>
            <WidePopup trigger={addNewLearnPathPopup}>
                <div className='d-flex justify-content-center w-100 fs-3'>Add new learn path</div>
                <div className='d-flex gap-3 mt-3'>
                    <label className='fs-4'>{t('title')}:</label>
                    <input type='text' className='w-50' value={newLearnPathTitle} onChange={handleNewLearnPathTitleChange} />
                </div>
                <div className='d-flex gap-3 mt-3'>
                    <label className='fs-4'>{t('description')}:</label>
                    <input type='text' className='w-50' value={newLearnPathDescription} onChange={handleNewLearnPathDescriptionChange} />
                </div>
                <div className='w-100 mt-3'>
                    <button className='float-start btn btn-danger' onClick={() => setAddNewLearnPathPopup(false)}>{t("cancel")}</button>
                    <button className='float-end btn btn-primary' onClick={addLearnPath}>{t("save")}</button>
                </div>
            </WidePopup>
            <WidePopup trigger={editTheoryPopup}>
                <div className='d-flex justify-content-center'>
                    <label className='fs-5'>{t("title")}: </label>
                    <input type='text' className='lesson-title-box' value={theoryTitle} onChange={handleTheoryTitleChange} />
                </div>
                <div className='d-flex'>
                    <textarea className='w-50' value={theoryText} onChange={handleTheoryTextChange} />
                    <div className='w-50 p-3' dangerouslySetInnerHTML={{ __html: theoryText }} />
                </div>
                <div className='w-100 mt-3'>
                    <button className='float-start btn btn-danger' onClick={() => setEditTheoryPopup(false)}>{t("cancel")}</button>
                    <button className='float-end btn btn-primary' onClick={saveLearnPathTheory}>{t("save")}</button>
                </div>
            </WidePopup>
            <WidePopup trigger={chooseQuestionsPopup}>
                {renderQuestions()}
                <div className='w-100 mt-3'>
                    <button className='float-start btn btn-danger' onClick={() => setChooseQuestionsPopup(false)}>{t("cancel")}</button>
                    <button className='float-end btn btn-primary' onClick={saveQuiz}>{t("save")}</button>
                </div>
            </WidePopup>
            <Popup trigger={confirmDeletePopup}>
                <div className='d-flex justify-content-center'>
                    {(() => {
                        switch (deletedElement) {
                            case SECTION:
                                return <p className='fs-4'>{t("delete_section_check")}</p>
                            case LESSON:
                                return <p className='fs-4'>{t("delete_lesson_check")}</p>
                            case THEORY:
                                return <p className='fs-4'>{t("delete_theory_check")}</p>
                            case QUIZ:
                                return <p className='fs-4'>{t("delete_quiz_check")}</p>
                            default:
                                return <p className='fs-4'>{t("delete_theory_check")}</p>
                        }
                    })()}
                </div>
                <div className='d-flex gap-3'>
                    {(() => {
                        switch (deletedElement) {
                            case SECTION:
                                return <label>{t("title")}:  {sectionName}</label>
                            case LESSON:
                                return <label>{t("title")}:  {lessonName}</label>
                            case THEORY:
                                return <label>{t("title")}:  {theoryTitle}</label>
                            case QUIZ:
                                return <label>{t("title")}:  {theoryTitle}</label>
                            default:
                                return
                        }
                    })()}
                </div>
                <br />
                <div className='w-100 d-flex justify-content-around'>
                    <button className='btn btn-primary' onClick={() => {
                        switch (deletedElement) {
                            case SECTION:
                                deleteLearnPathSection(deletedElementId)
                                break
                            case LESSON:
                                deleteLearnPathLesson(deletedElementId)
                                break
                            case THEORY:
                                deleteLearnPathTheory(deletedElementId)
                                break
                            case QUIZ:
                                deleteLearnPathQuiz(deletedElementId)
                                break
                        }
                    }}>{t("yes")}</button>
                    <button className='btn btn-danger' onClick={() => setConfirmDeletePopup(false)}>{t("no")}</button>
                </div>
            </Popup>
        </div>
    );
};

export default LearnPath;
