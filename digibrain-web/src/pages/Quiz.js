import React, { useState } from 'react';
import Form from 'react-bootstrap/Form';

import '../App.css'
import Popup from '../components/Popup';
import apiService from '../services/ApiService';
import WidePopup from '../components/WidePopup';

function Quiz() {

    const [popup, setPopup] = useState(false)
    const [addQuestionPopup, setAddQuestionPopup] = useState(false)
    const [confirmDeletePopup, setConfirmDeletePopup] = useState(false)

    const [classNumber, setClassNumber] = useState(0)
    const [university, setUniversity] = useState(false)

    const [subjects, setSubjects] = useState([])
    const [questions, setQuestions] = useState([])

    const [selectedClassId, setSelectedClassId] = useState(0)
    const [selectedSubjectId, setSelectedSubjectId] = useState(0)
    const [selectedDifficuty, setSelectedDificulty] = useState('')
    const [selectedQuestionType, setSelectedQuestionType] = useState('')
    const [selectedQuestion, setSelectedQuestion] = useState(null)

    const [questionsOpened, setQuestionsOpened] = useState(false)
    const [questionText, setQuestionText] = useState('')
    const [addedAnswers, setAddedAnswers] = useState([])

    const token = sessionStorage.getItem('token')
    const config = { headers: { 'Authorization': 'Bearer ' + token } }

    let subjectsList = []

    const handleButtonClick = (number, university) => {
        setClassNumber(number)
        setUniversity(university)
    }

    const handleSelectChange = (event) => {
        setSelectedSubjectId(event.target.value)
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

    const handleAddQuestionPopupOpen = () => {
        setAddQuestionPopup(true)
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
                    text: "Adevarat",
                    position: 0,
                    correct: false,
                    questionId: 0
                }
                let falseItem = {
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
            text: "",
            position: position,
            correct: correct,
            questionId: 0
        }

        updatedAnswers.push(answerListItem)
        setAddedAnswers(updatedAnswers)
    }

    const handleEditQuestion = (question) => {
        setSelectedQuestion(question)
        setQuestionText(question.text)
        const answers = []
        question.answers.forEach(answer => {
            let item = {
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
            <div id="accordion" className='scrollable'>
                {questions.map((item, _) => (
                    <div className="card" key={item.id}>
                        <div className="card-header">
                            <h5 className="mb-0">
                                <div className='float-start'>
                                    <button className="btn btn-link" onClick={() => handleAccordionToggle(item.id)}>
                                        {item.text}
                                    </button>
                                </div>
                                <div className='float-end'>
                                    <div className='d-flex gap-2'>
                                        <i className='fa fa-edit icon' onClick={() => handleEditQuestion(item)} />
                                        <i className='fa fa-trash icon' onClick={() => handleDeleteQuestion(item)} />
                                    </div>
                                </div>
                            </h5>
                        </div>

                        <div className={"collapse" + (activeKey === item.id ? ' show' : '')}>
                            <div className="card-body">
                                <ul>
                                    {item.answers.map((answer, _) => (
                                        <li key={answer.id}>{answer.text} - {answer.correct ? 'Corect' : 'Gresit'} - {answer.position}</li>
                                    ))}
                                </ul>
                            </div>
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

    const addQuestion = () => {

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
    }

    const [activeKey, setActiveKey] = useState(0);

    const handleAccordionToggle = (selectedToggle) => {
        if (activeKey === selectedToggle)
            setActiveKey('')
        else
            setActiveKey(selectedToggle);
    };

    subjectsList = [
        <option value={0} key={0}>-- Choose one subject --</option>,
        subjects.map((item, _) => (
            <option value={item.id} key={item.id}>{item.name}</option>
        ))
    ]

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
            </div>
            <div className='d-flex align-items-center'>
                <p className='m-3'>Alege dificultate</p>
                <div className='btn-group'>
                    <button className={'btn' + (selectedDifficuty === 'Easy' ? ' bg-primary text-white' : '')} onClick={() => setSelectedDificulty('Easy')}>Easy</button>
                    <button className={'btn' + (selectedDifficuty === 'Medium' ? ' bg-primary text-white' : '')} onClick={() => setSelectedDificulty('Medium')}>Medium</button>
                    <button className={'btn' + (selectedDifficuty === 'Hard' ? ' bg-primary text-white' : '')} onClick={() => setSelectedDificulty('Hard')}>Hard</button>
                </div>
            </div>
            <div className='d-flex align-items-center'>
                <p className='m-3'>Alege tipul de Quiz</p>
                <div className='btn-group'>
                    <button className={'btn' + (selectedQuestionType === 'MultipleChoice' ? ' bg-primary text-white' : '')} onClick={() => setSelectedQuestionType('MultipleChoice')}>Raspuns multiplu</button>
                    <button className={'btn' + (selectedQuestionType === 'WordsGap' ? ' bg-primary text-white' : '')} onClick={() => setSelectedQuestionType('WordsGap')}>Cuvinte lipsa</button>
                    <button className={'btn' + (selectedQuestionType === 'TrueFalse' ? ' bg-primary text-white' : '')} onClick={() => setSelectedQuestionType('TrueFalse')}>Adevarat sau Fals</button>
                </div>
            </div>
            <div>
                <button onClick={getQuestions}>Open</button>
            </div>
            {renderQuestions()}
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
            <WidePopup trigger={addQuestionPopup}>
                <div className='d-flex'>
                    <label className='w-25'>Question text:</label>
                    <input className='w-75' type='text' value={questionText} onChange={handleQuestionTextChange} />
                </div>
                <label>Answers</label>
                <div>
                    {(selectedQuestionType === "MultipleChoice") &&
                        <>
                            <ul>
                                {addedAnswers.map((item, position) => (
                                    <div key={position} className='d-flex gap-5'>
                                        <label>Answer {position + 1}</label>
                                        <input className='ml-3' type='text' value={item.text} onChange={(event) => handleAddAnswerTextChange(event, position)} />
                                        <Form.Check type='checkbox' checked={item.correct} onChange={(event) => handleAddAnswerCheckChange(event, position)} />
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
                                    <label>Position {position + 1}</label>
                                    <input className='ml-3' type='text' value={item.text} onChange={(event) => handleAddAnswerTextChange(event, position)} />
                                </div>
                            ))}
                        </ul>
                    }
                </div>
                <div className='w-100'>
                    <button className='float-start' onClick={handleAddQuestionPopupClose}>Cancel</button>
                    <button className='float-end' onClick={addQuestion}>Save</button>
                </div>
            </WidePopup>
            <Popup trigger={confirmDeletePopup}>
                <div className='d-flex justify-content-center'>
                    <p>Sigur vrei sa stergi aceasta intrebare?</p>
                </div>
                <div className='d-flex gap-3'>
                    <label>Intrebare:</label>
                    {(selectedQuestion !== null) &&
                        <label>{selectedQuestion.text}</label>
                    }
                </div>
                <br />
                <div className='w-100 d-flex justify-content-around'>
                    <button onClick={deleteSelectedQuestion}>Da</button>
                    <button onClick={() => setConfirmDeletePopup(false)}>Nu</button>
                </div>
            </Popup>
        </div>
    );
};

export default Quiz;
