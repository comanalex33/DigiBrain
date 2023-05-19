import React, { useState } from 'react';
import Form from 'react-bootstrap/Form';

import '../App.css'
import Popup from '../components/Popup';
import apiService from '../services/ApiService';

function Quiz() {

    const [popup, setPopup] = useState(false)

    const [classNumber, setClassNumber] = useState(0)
    const [university, setUniversity] = useState(false)

    const [subjects, setSubjects] = useState([])
    const [questions, setQuestions] = useState([])

    const [selectedClassId, setSelectedClassId] = useState(0)
    const [selectedSubjectId, setSelectedSubjectId] = useState(0)
    const [selectedDifficuty, setSelectedDificulty] = useState('')
    const [selectedQuestionType, setSelectedQuestionType] = useState('')

    let subjectsList = []

    const handleButtonClick = (number, university) => {
        setClassNumber(number)
        setUniversity(university)
    }

    const handleSelectChange = (event) => {
        setSelectedSubjectId(event.target.value)
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
        if(selectedSubjectId !== 0 && selectedDifficuty !== '' && selectedQuestionType != '') {
            apiService.getQuestionsForSubject(selectedSubjectId, 2, selectedDifficuty, selectedQuestionType, 2)
                .then(response => {
                    setQuestions(response.data)
                    console.log(response.data)
                })
                .catch(error => {
                    console.log(error)
                })
        }
    }

    const renderQuestions = () => {
        return (
            <div id="accordion">
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
                                    <i className='fa fa-edit icon'/>
                                </div>
                            </h5>
                        </div>

                        <div className={"collapse" + (activeKey === item.id ? ' show' : '')}>
                            {/* <div className="card-body" dangerouslySetInnerHTML={{ __html: item.text }} /> */}
                            {renderAnswers(item.id)}
                        </div>
                    </div>
                ))}
            </div>
        )
    }

    const renderAnswers = (questionId) => {
        apiService.getAnswersForQuestion(questionId)
            .then(response => {
                console.log(response.data)
                return (
                    <div className="card-body">
                        {response.data.map((item, _) => (
                            <p>{item.text}</p>
                        ))}
                    </div>
                )
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
                    <button className={'btn' + (selectedDifficuty === 'Easy' ? ' bg-primary text-white': '')} onClick={() => setSelectedDificulty('Easy')}>Easy</button>
                    <button className={'btn' + (selectedDifficuty === 'Medium' ? ' bg-primary text-white': '')} onClick={() => setSelectedDificulty('Medium')}>Medium</button>
                    <button className={'btn' + (selectedDifficuty === 'Hard' ? ' bg-primary text-white': '')} onClick={() => setSelectedDificulty('Hard')}>Hard</button>
                </div>
            </div>
            <div className='d-flex align-items-center'>
                <p className='m-3'>Alege tipul de Quiz</p>
                <div className='btn-group'>
                    <button className={'btn' + (selectedQuestionType === 'MultipleChoice' ? ' bg-primary text-white': '')} onClick={() => setSelectedQuestionType('MultipleChoice')}>Raspuns multiplu</button>
                    <button className={'btn' + (selectedQuestionType === 'WordsGap' ? ' bg-primary text-white': '')} onClick={() => setSelectedQuestionType('WordsGap')}>Cuvinte lipsa</button>
                    <button className={'btn' + (selectedQuestionType === 'TrueFalse' ? ' bg-primary text-white': '')} onClick={() => setSelectedQuestionType('TrueFalse')}>Adevarat sau Fals</button>
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
        </div>
    );
};

export default Quiz;
