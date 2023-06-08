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

function LearnPath() {

    // Explorer mode
    const [popup, setPopup] = useState(false)

    const [activeKey, setActiveKey] = useState(0);

    const [classNumber, setClassNumber] = useState(0)
    const [university, setUniversity] = useState(false)
    const [languageId, setLanguageId] = useState(0)

    const [subjects, setSubjects] = useState([])

    const [learnPathsOpened, setLearnPathsOpened] = useState(false)
    const [learnPaths, setLearnPaths] = useState([])
    const [filteredLearnPaths, setFilteredLearnPaths] = useState([])

    const [selectedClassId, setSelectedClassId] = useState(0)
    const [selectedSubjectId, setSelectedSubjectId] = useState(0)
    const [selectedSubjectName, setSelectedSubjectName] = useState('')

    const [searchFilterActive, setSearchFilterActive] = useState(true);

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
        setLearnPathTitle(learnPath.title)
        setLearnPathDescription(learnPath.description)
        openEditMode()
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

    const getLearnPaths = () => {
        apiService.getLearnPaths()
            .then(response => {
                console.log(response.data)
                setLearnPaths(response.data)
            })
            .catch(error => {
                console.log(error)
            })
    }

    const filterLearnPaths = () => {
        console.log(selectedSubjectId)
        if (selectedSubjectId !== 0) {
            const filteredList = learnPaths.filter((item) => item.subjectId === parseInt(selectedSubjectId))
            console.log(filteredList)
            setFilteredLearnPaths(filteredList)
            setLearnPathsOpened(true)
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

    const [addLessonOpen, setAddLessonOpen] = useState(false)
    const [lessonName, setLessonName] = useState('')

    const [editTheoryPopup, setEditTheoryPopup] = useState(false)
    const [editedTheoryId, setEditedTheoryId] = useState(0)
    const [theoryTitle, setTheoryTitle] = useState('')
    const [theoryText, setTheoryText] = useState('')

    const [chooseQuestionsPopup, setChooseQuestionsPopup] = useState(false)
    const [questions, setQuestions] = useState([])
    const [activeQuestionId, setActiveQuestionId] = useState(null)
    const [selectedQuestionIds, setSelectedQuestionIds] = useState([])

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

    const getLearnPathDetails = (id) => {
        apiService.getLearnPathDetails(id)
            .then(response => {
                setDetailedLearnPath(response.data)
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

    const saveQuiz = () => {
        console.log(selectedQuestionIds)
        setChooseQuestionsPopup(false)
        setSelectedQuestionIds([])
    }

    const addLearnPathSection = () => {
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
    }

    const addLearnPathLesson = () => {
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
    }

    const addLearnPathTheory = () => {
        if (theoryTitle !== null && theoryText !== null) {
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
        }
    }

    const renderLearnPathSections = () => {
        return (
            <div className="row mt-3">
                <div className="col-4">
                    <ListGroup id="list-tab">
                        {(detailedLearnPath !== null) &&
                            <>
                                {detailedLearnPath.sections.map((section, _) => (
                                    <ListGroup.Item key={section.id} action onClick={() => handleLearnPathSectionChange(section)} eventKey={section.id}>
                                        {/* {section.title} */}
                                        <div className='d-flex justify-content-between p-1'>
                                            <label>{section.title}</label>
                                            <i className='fa-solid fa-edit icon' />
                                        </div>
                                    </ListGroup.Item>
                                ))}
                                <div className={'d-inline d-flex align-items-center' + (addSectionOpen === false ? ' d-none' : '')}>
                                    <input type='text' className='m-1 w-100' onChange={handleSectionNameChange} />
                                    <i className='fa fa-check icon m-2' onClick={addLearnPathSection} />
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
                            activeSection.lessons.map((lesson, _) => (
                                <div className="card" key={lesson.id}>
                                    <div className="card-header">
                                        <h5 className="mb-0">
                                            <div className='float-start'>
                                                <button className="btn btn-link" onClick={() => handleLearnPathLessonChange(lesson)}>
                                                    {lesson.title}
                                                </button>
                                            </div>
                                        </h5>
                                    </div>

                                    {(activeLesson !== null) &&
                                        <div className={"collapse" + (activeLesson === lesson ? ' show' : '')}>
                                            <p className='m-2'>{t("lessons")}</p>
                                            {activeLesson.theory.map((theory, position) => (
                                                <div key={position} className='card m-1'>
                                                    <div className='d-flex justify-content-between p-3'>
                                                        <label>{theory.title}</label>
                                                        <i className='fa-solid fa-edit icon' onClick={() => handleTheoryEdit(theory)} />
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
                                                        <i className='fa-solid fa-edit icon' onClick={() => handleQuizEdit(activeLesson.quiz)} />
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
                            <input type='text' className='m-1 w-100' onChange={handleLessonNameChange} />
                            <i className='fa fa-check icon m-2' onClick={addLearnPathLesson} />
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
    }

    return (
        <div className='Page-fit overflow-hidden' id='container'>
            {/* Learn Path explorer mode */}
            <div className='step p-3 d-flex flex-column' id='step1'>
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
                            <button onClick={filterLearnPaths} className='btn btn-primary w-25 fs-5'>{t("open")}</button>
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
                    </div>
                }
            </div>
            {/* Learn Path edit mode */}
            <div className='step p-3 overflow-auto scrollable' id='step2'>
                <div className='d-flex justify-content-between w-100 p-3'>
                    <button className='btn btn-danger' onClick={closeEditMode}>{t("cancel")}</button>
                    <button className='btn btn-primary'>{t("save")}</button>
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
                    <button className='float-end btn btn-primary' onClick={addLearnPathTheory}>{t("save")}</button>
                </div>
            </WidePopup>
            <WidePopup trigger={chooseQuestionsPopup}>
                {renderQuestions()}
                <div className='w-100 mt-3'>
                    <button className='float-start btn btn-danger' onClick={() => setChooseQuestionsPopup(false)}>{t("cancel")}</button>
                    <button className='float-end btn btn-primary' onClick={saveQuiz}>{t("save")}</button>
                </div>
            </WidePopup>
        </div>
    );
};

export default LearnPath;
