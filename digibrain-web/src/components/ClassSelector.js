import React from 'react'
import '../App.css'

function ClassSelector(props) {

    const classNumber = props.classNumber
    const university = props.university

    const handleClassSelected = props.handleClassSelected
    const translator = props.translator

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
                            onClick={() => handleClassSelected(number, false)}
                            className={classNumber === number && university === false ? 'Learn-selected-button btn' : 'btn bg-secondary text-white'}
                            style={{ width: 12 + '%' }}>
                            {number}
                        </button>
                    ))}
                </div>
                <br />
                <div className='d-flex w-100 justify-content-around'>
                    {secondRow.map((number) => (
                        <button
                            key={number}
                            onClick={() => handleClassSelected(number, false)}
                            className={classNumber === number && university === false ? 'Learn-selected-button btn' : 'btn bg-secondary text-white'}
                            style={{ width: 12 + '%' }}>
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
                            onClick={() => handleClassSelected(number, false)}
                            className={classNumber === number && university === false ? 'Learn-selected-button btn' : 'btn bg-secondary text-white'}
                            style={{ width: 12 + '%' }}>
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
                            onClick={() => handleClassSelected(number, true)}
                            className={classNumber === number && university === true ? 'Learn-selected-button btn' : 'btn bg-secondary text-white'}
                            style={{ width: 12 + '%' }}>
                            {getRomanFromNumber(number)}
                        </button>
                    ))}
                </div>
                <br />
                <div className='d-flex w-100 justify-content-around'>
                    {secondRow.map((number) => (
                        <button
                            key={number}
                            onClick={() => handleClassSelected(number, true)}
                            className={classNumber === number && university === true ? 'Learn-selected-button btn' : 'btn bg-secondary text-white' +
                                (number === 7 || number === 8 ? ' invisible' : '')}
                            style={{ width: 12 + '%' }}>
                            {getRomanFromNumber(number)}
                        </button>
                    ))}
                </div>
            </div>
        );
    }

    return (
        <>
            <h2 id='title-accept-delete'>{translator("school")}</h2>
            {renderSchoolButtons()}
            <h2 id='title-accept-delete'>{translator("highschool")}</h2>
            {renderHighschoolButtons()}
            <h2 id='title-accept-delete'>{translator("university")}</h2>
            {renderUniversityButtons()}
        </>
    )
}

export default ClassSelector;
