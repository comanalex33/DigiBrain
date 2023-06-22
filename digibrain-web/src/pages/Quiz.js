import React, { useState, useEffect } from 'react';
import Form from 'react-bootstrap/Form';
import { useTranslation } from 'react-i18next';

import '../App.css'
import Popup from '../components/Popup';
import WidePopup from '../components/WidePopup';
import ClassSelector from '../components/ClassSelector';

import apiService from '../services/ApiService';

function Quiz() {

    const [popup, setPopup] = useState(false)
    const [addQuestionPopup, setAddQuestionPopup] = useState(false)
    const [confirmDeletePopup, setConfirmDeletePopup] = useState(false)

    const [classNumber, setClassNumber] = useState(0)
    const [university, setUniversity] = useState(false)
    const [languageId, setLanguageId] = useState(0)

    const [subjects, setSubjects] = useState([])
    const [questions, setQuestions] = useState([])
    const [editQuestion, setEditQuestion] = useState(false)

    const [selectedClassId, setSelectedClassId] = useState(0)
    const [selectedSubjectId, setSelectedSubjectId] = useState(0)
    const [selectedSubjectName, setSelectedSubjectName] = useState('')
    const [selectedDifficuty, setSelectedDificulty] = useState('')
    const [selectedQuestionType, setSelectedQuestionType] = useState('')
    const [selectedQuestion, setSelectedQuestion] = useState(null)

    const [questionsOpened, setQuestionsOpened] = useState(false)
    const [questionText, setQuestionText] = useState('')
    const [addedAnswers, setAddedAnswers] = useState([])

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
                setQuestionsOpened(false)
                setSelectedSubjectName('')
                setSelectedSubjectId(0)
                setQuestions([])
                setSelectedQuestion(null)
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

    const handleQuestionTextChange = event => {
        setQuestionText(event.target.value)

        if (selectedQuestionType === "WordsGap") {
            const count = countOccurrences(event.target.value, "__")
            if (addedAnswers.length < count) {
                addNewAnswer(count, true)
            } else if (addedAnswers.length > count) {
                const updatedAnswers = [...addedAnswers]
                updatedAnswers.pop()
                setAddedAnswers(updatedAnswers)
            }
        }
    }

    const handleAddAnswerTextChange = (event, position) => {
        const updatedAnswers = [...addedAnswers]
        updatedAnswers[position].text = event.target.value
        setAddedAnswers(updatedAnswers)
    }

    const handleAddAnswerCheckChange = (event, position) => {
        const updatedAnswers = [...addedAnswers]
        updatedAnswers[position].correct = event.target.checked
        setAddedAnswers(updatedAnswers)
    }

    const handleAddQuestionPopupClose = () => {
        setAddQuestionPopup(false)
        setAddedAnswers([])
    }

    const handleSearchFilterChange = () => {
        setSearchFilterActive(!searchFilterActive)
    };

    const handleAddQuestionPopupOpen = () => {
        setAddQuestionPopup(true)
        setEditQuestion(false)
        setQuestionText("")

        switch (selectedQuestionType) {
            case "MultipleChoice":
                setAddedAnswers([])
                return
            case "WordsGap":
                setAddedAnswers([])
                return
            case "TrueFalse":
                const updatedAnswers = []
                let trueItem = {
                    id: 0,
                    text: "Adevarat",
                    position: 0,
                    correct: false,
                    questionId: 0
                }
                let falseItem = {
                    id: 0,
                    text: "Fals",
                    position: 0,
                    correct: false,
                    questionId: 0
                }
                updatedAnswers.push(trueItem)
                updatedAnswers.push(falseItem)
                setAddedAnswers(updatedAnswers)
                return
            default:
                return
        }
    }

    const addNewAnswer = (position = 0, correct = false) => {
        console.log(addedAnswers)
        const updatedAnswers = [...addedAnswers]

        let answerListItem = {
            id: 0,
            text: "",
            position: position,
            correct: correct,
            questionId: (editQuestion === true) ? selectedQuestion.id : 0
        }

        updatedAnswers.push(answerListItem)
        setAddedAnswers(updatedAnswers)
    }

    const removeAnswer = (position) => {
        const updatedAnswers = [...addedAnswers]

        updatedAnswers.splice(position, 1)
        setAddedAnswers(updatedAnswers)
    }

    const handleEditQuestion = (question) => {
        setSelectedQuestion(question)
        setEditQuestion(true)
        setQuestionText(question.text)
        const answers = []
        question.answers.forEach(answer => {
            let item = {
                id: answer.id,
                text: answer.text,
                position: answer.position,
                correct: answer.correct,
                questionId: answer.questionId
            }
            answers.push(item)
        });

        setAddedAnswers(answers)
        setAddQuestionPopup(true)
    }

    const handleDeleteQuestion = (question) => {
        setSelectedQuestion(question)
        setConfirmDeletePopup(true)
    }

    const countOccurrences = (string, substring) => {

        const regex = new RegExp(substring, "gi");
        const matches = string.match(regex);

        const count = matches ? matches.length : 0;

        return count
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

    const getQuestions = () => {
        if (selectedSubjectId !== 0 && selectedDifficuty !== '' && selectedQuestionType != '') {
            apiService.getQuestionsForSubject(selectedSubjectId, 0, selectedDifficuty, selectedQuestionType, 2)
                .then(response => {
                    setQuestionsOpened(true)
                    setQuestions(response.data)
                    console.log(response.data)
                })
                .catch(error => {
                    console.log(error)
                })
        }
    }

    const deleteSelectedQuestion = () => {
        apiService.deleteQuestion(selectedQuestion.id, config)
            .then(response => {
                console.log("Question deleted")
                getQuestions()
                setConfirmDeletePopup(false)
            })
            .catch(error => {
                console.log(error)
            })
    }

    const renderQuestions = () => {
        return (
            <div id="accordion" className='scrollable mt-3 overflow-auto'>
                {questions.map((item, _) => (
                    <div className="card" key={item.id}>
                        <div className="card-header">
                            <h5 className="mb-0">
                                <div className='float-start h-100'>
                                    <div className='fs-6 d-flex align-items-center'>{item.text}</div>
                                </div>
                                <div className='float-end'>
                                    <div className='d-flex gap-2'>
                                        <i className='fa fa-edit icon' onClick={() => handleEditQuestion(item)} />
                                        <i className='fa fa-trash icon' onClick={() => handleDeleteQuestion(item)} />
                                    </div>
                                </div>
                            </h5>
                        </div>
                    </div>
                ))}
                {(questionsOpened === true) &&
                    <div className='card card-add m-3' onClick={handleAddQuestionPopupOpen}>
                        <div className="card-header d-flex justify-content-center">
                            <i className='fa fa-plus icon' />
                        </div>
                    </div>
                }
            </div>
        )
    }

    const saveQuestion = () => {

        if (addedAnswers.length < 2) {
            alert("Orice intrebare trebuie sa aiba si raspunsuri, cel putin 2")
            return
        }

        for (let i = 0; i < addedAnswers.length; i++) {
            if (addedAnswers[i].text === "") {
                alert("Toate raspunsurile trebuie sa aiba text")
                return
            }
        }

        if (editQuestion === false) {
            apiService.addQuestion(questionText, selectedDifficuty, selectedQuestionType, 2, config)
                .then(questionResponse => {
                    const updatedAnswers = [...addedAnswers]
                    updatedAnswers.forEach(element => {
                        element.questionId = questionResponse.data.id
                    });

                    apiService.addAnswers(updatedAnswers, config)
                        .then(answerResponse => {
                            apiService.addQuestionToSubjects([selectedSubjectId], questionResponse.data.id, config)
                                .then(response => {
                                    console.log("Question added")
                                    getQuestions()
                                    setAddQuestionPopup(false)
                                })
                                .catch(error => {
                                    console.log(error)
                                })
                        })
                        .catch(error => {
                            console.log(error)
                        })
                })
                .catch(error => {
                    console.log(error)
                })
        } else {
            apiService.updateQuestion(selectedQuestion.id, questionText, selectedDifficuty, selectedQuestionType, languageId, addedAnswers, config)
                .then(response => {
                    console.log("Question updated")
                    getQuestions()
                    setAddQuestionPopup(false)
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

    return (
        <div className='Page-fit p-3 d-flex flex-column'>

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


                    {/* Choose difficulty */}
                    <div className='d-flex align-items-center'>
                        <p className='fs-4 fw-bold mt-3'>{t("select_difficulty")}</p>
                        <div className='btn-group m-3'>
                            <button className={'btn' + (selectedDifficuty === 'Easy' ? ' bg-primary text-white' : '')} onClick={() => setSelectedDificulty('Easy')}>{t("easy")}</button>
                            <button className={'btn' + (selectedDifficuty === 'Medium' ? ' bg-primary text-white' : '')} onClick={() => setSelectedDificulty('Medium')}>{t("medium")}</button>
                            <button className={'btn' + (selectedDifficuty === 'Hard' ? ' bg-primary text-white' : '')} onClick={() => setSelectedDificulty('Hard')}>{t("hard")}</button>
                        </div>
                    </div>

                    {/* Choose question type */}
                    <div className='d-flex align-items-center'>
                        <p className='fs-4 fw-bold mt-3'>{t("select_question_type")}</p>
                        <div className='btn-group m-3'>
                            <button className={'btn' + (selectedQuestionType === 'MultipleChoice' ? ' bg-primary text-white' : '')} onClick={() => setSelectedQuestionType('MultipleChoice')}>{t("multiple_choice")}</button>
                            <button className={'btn' + (selectedQuestionType === 'WordsGap' ? ' bg-primary text-white' : '')} onClick={() => setSelectedQuestionType('WordsGap')}>{t("words_gap")}</button>
                            <button className={'btn' + (selectedQuestionType === 'TrueFalse' ? ' bg-primary text-white' : '')} onClick={() => setSelectedQuestionType('TrueFalse')}>{t("true_false")}</button>
                        </div>
                    </div>

                    {/* Open questions */}
                    <div className='d-flex justify-content-center mb-3'>
                        <button onClick={getQuestions} className='btn btn-primary w-25 fs-5'>{t("open")}</button>
                    </div>
                </div>
            </div>
            {(questionsOpened === false) ?
                <div className='flex-grow-1 d-flex align-items-center justify-content-center fs-4 text-danger'>{t("no_questions_loaded")}</div>
                :
                <>
                    {renderQuestions()}
                </>
            }
            <Popup trigger={popup}>
                <ClassSelector classNumber={classNumber} university={university} handleClassSelected={handleButtonClick} translator={t} />
                <div className='d-flex justify-content-end'>
                    <button className='btn btn-primary' onClick={() => getSubjects(0)}>Ok</button>
                </div>
            </Popup>
            <WidePopup trigger={addQuestionPopup}>
                <div className='d-flex'>
                    <label className='w-25'>{t("question_text")}:</label>
                    <input className='w-100' type='text' value={questionText} onChange={handleQuestionTextChange} />
                </div>
                <label>{t("answers")}</label>
                <div>
                    {(selectedQuestionType === "MultipleChoice") &&
                        <>
                            <ul>
                                {addedAnswers.map((item, position) => (
                                    <div key={position} className='d-flex gap-5'>
                                        <div className='d-flex justify-content-between p-1 w-100'>
                                            <div className='d-flex gap-3'>
                                                <label>{t("answer")} {position + 1}</label>
                                                <input className='ml-3' type='text' value={item.text} onChange={(event) => handleAddAnswerTextChange(event, position)} />
                                                <Form.Check type='checkbox' checked={item.correct} onChange={(event) => handleAddAnswerCheckChange(event, position)} />
                                            </div>
                                            <i className='fa fa-trash icon' onClick={() => removeAnswer(position)} />
                                        </div>
                                    </div>
                                ))}
                            </ul>
                            <div className='card card-add m-3' onClick={() => addNewAnswer()}>
                                <div className="card-header d-flex justify-content-center">
                                    <i className='fa fa-plus icon' />
                                </div>
                            </div>
                        </>
                    }
                    {(selectedQuestionType === "TrueFalse") &&
                        <ul>
                            {addedAnswers.map((item, position) => (
                                <div key={position} className='d-flex gap-5'>
                                    <label>{item.text}</label>
                                    <Form.Check type='checkbox' checked={item.correct} onChange={(event) => handleAddAnswerCheckChange(event, position)} />
                                </div>
                            ))}
                        </ul>
                    }
                    {(selectedQuestionType === "WordsGap") &&
                        <ul>
                            {addedAnswers.map((item, position) => (
                                <div key={position} className='d-flex gap-5'>
                                    <label>{t("position")} {position + 1}</label>
                                    <input className='ml-3' type='text' value={item.text} onChange={(event) => handleAddAnswerTextChange(event, position)} />
                                </div>
                            ))}
                        </ul>
                    }
                </div>
                <div className='w-100'>
                    <button className='float-start btn btn-danger' onClick={handleAddQuestionPopupClose}>{t("cancel")}</button>
                    <button className='float-end btn btn-primary' onClick={saveQuestion}>{t("save")}</button>
                </div>
            </WidePopup>
            <Popup trigger={confirmDeletePopup}>
                <div className='d-flex justify-content-center'>
                    <p className='fs-4'>{t("delete_question_check")}</p>
                </div>
                <div className='d-flex gap-3'>
                    <label>{t("question")}:</label>
                    {(selectedQuestion !== null) &&
                        <label>{selectedQuestion.text}</label>
                    }
                </div>
                <br />
                <div className='w-100 d-flex justify-content-around'>
                    <button className='btn btn-primary' onClick={deleteSelectedQuestion}>{t("yes")}</button>
                    <button className='btn btn-danger' onClick={() => setConfirmDeletePopup(false)}>{t("no")}</button>
                </div>
            </Popup>
        </div>
    );
};

export default Quiz;
