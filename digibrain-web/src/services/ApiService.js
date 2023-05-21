import axios from 'axios';

const API_BASE_URL = 'http://ec2-3-65-36-184.eu-central-1.compute.amazonaws.com';

const apiService = {

    // Authentication
    login: (username, password) => {
        let user = {
            username: username,
            password: password
        }

        return axios.post(`${API_BASE_URL}/api/Auth/login`, user);
    },
    getObjectStorageInfo: (config) => {
        return axios.get(`${API_BASE_URL}/api/Auth/object-storage-info`, config)
    },

    // User
    getUserDetails: (username, config) => {
        return axios.get(`${API_BASE_URL}/api/users/${username}`, config)
    },

    // Learn
    // -- GET --
    getClassByNumberAndDomain: (classNumber, university, domainId) => {
        return axios.get(`${API_BASE_URL}/api/classes`, {
            params: {
                Number: classNumber,
                AtUniversity: university,
                DomainId: domainId
            }
        })
    },
    getSubjects: (classId, languageId) => {
        return axios.get(`${API_BASE_URL}/api/subjects/class/${classId}/languages/${languageId}`)
    },
    getChapters: (subjectId) => {
        return axios.get(`${API_BASE_URL}/api/chapters/subject/${subjectId}`)
    },
    getLessons: (chapterId) => {
        return axios.get(`${API_BASE_URL}/api/lessons/chapter/${chapterId}`)
    },
    // -- POST --
    addLesson: (title, text, chapterId, config) => {
        let lesson = {
            title: title,
            text: text,
            chapterId: chapterId
        }

        return axios.post(`${API_BASE_URL}/api/lessons`, lesson, config);
    },
    addChapter: (number, name, subjectId, config) => {
        let chapter = {
            number: number,
            name: name,
            subjectId: subjectId
        }

        return axios.post(`${API_BASE_URL}/api/chapters`, chapter, config);
    },
    addSubject: (subjectName, classId, languageId, iconId, config) => {
        let subject = {
            name: subjectName,
            classId: classId,
            languageId: languageId,
            iconId: iconId
        }

        return axios.post(`${API_BASE_URL}/api/subjects`, subject, config);
    },

    // Quiz
    // -- GET --
    getQuestionsForSubject: (subjectId, numberOfQuestions, difficulty, questionType, languageId) => {
        let questionParams = {
            number: numberOfQuestions,
            difficulty: difficulty,
            type: questionType,
            languageId: languageId
        }

        return axios.get(`${API_BASE_URL}/api/questions/subject/${subjectId}/answers`, { params: questionParams })
    },
    getAnswersForQuestion: (questionId) => {
        return axios.get(`${API_BASE_URL}/api/answers/questions/${questionId}`)
    },
    addQuestion: (text, difficulty, questionType, languageId, config) => {
        let question = {
            text: text,
            difficulty: difficulty,
            type: questionType,
            languageId: languageId
        }

        return axios.post(`${API_BASE_URL}/api/questions`, question, config)
    },
    addAnswers: (answers, config) => {
        return axios.post(`${API_BASE_URL}/api/answers/multiple`, answers, config)
    },
    addQuestionToSubjects: (subjectIds, questionId, config) => {
        return axios.post(`${API_BASE_URL}/api/questions/${questionId}/subjects`, subjectIds, config)
    },
    // -- DELETE --
    deleteQuestion: (id, config) => {
        return axios.delete(`${API_BASE_URL}/api/questions/${id}`, config)
    }
};

export default apiService;