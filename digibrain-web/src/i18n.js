import i18n from "i18next";
import i18nBackend from "i18next-http-backend";
import { initReactI18next } from "react-i18next";

i18n
    .use(i18nBackend)
    .use(initReactI18next)
    .init({
        fallbackLng: 'en',
        lng: 'en',
        interpolation: {
            escapeValue: false
        },
        resources: {
            en: {
                translation: {
                    // General
                    true: "True",
                    false: "False",
                    yes: "Yes",
                    no: "No",
                    correct: "Correct",
                    wrong: "Wrong",
                    open: "Open",
                    cancel: "Cancel",
                    save: "Save",
                    title: "Title",
                    school: "School",
                    highschool: "Highschool",
                    university: "University",

                    // Login
                    login: "Login",
                    register: "Register",
                    sign_in: "Sign In",
                    sign_up: "Sign Up",
                    username: "Username",
                    email: "Email",
                    password: "Password",

                    // Navbar
                    learn_tab: "Learn",
                    quiz_tab: "Quiz",
                    learn_path_tab: "Learn Path",
                    sign_in_button: "Sign In",
                    sign_out_button: "Sign Out",

                    // Home
                    home_page_title: "Digi Brain Administration",
                    home_page_description: "This is the administration page for Digi Brain mobile app. This page is designed for administrators and teachers to make their work easier by having a place where they can manage all the educational materials, including lessons, questions and learning paths.",
                    home_page_motto: "Explore, Learn, Succeed: Your Educational Journey Starts Here",

                    // Learn
                    choose_class: "Choose class",
                    select_class: "Select class",
                    selected_class: "Selected class",
                    no_class_selected: "No class selected",
                    at_university: "At university",
                    choose_subject: "Choose one subject",
                    select_subject: "Select subject",
                    selected_subject: "Selected subject",
                    no_subject_selected: "No subject selected",
                    subject_content: "Subject content",
                    search_filter: "Search filter",
                    no_subject_loaded: "No subject loaded yet",

                    // Quiz
                    select_difficulty: "Select difficulty",
                    easy: "Easy",
                    medium: "Medium",
                    hard: "Hard",
                    select_question_type: "Select question type",
                    multiple_choice: "Multiple Choice",
                    words_gap: "Words Gap",
                    true_false: "True or False",
                    question_text: "Question text",
                    answers: "Answers",
                    answer: "Answer",
                    position: "Position",
                    delete_question_check: "Are you sure you want to delete this question?",
                    question: "Question",
                    no_questions_loaded: "No questions loaded yet",

                    // Learn Path
                    lessons: "Lessons",
                    quiz: "Quiz",
                    author: "Author",
                    description: "Description",
                    last_updated: "Last updated",
                    started: "Started",
                    times: "times",
                    no_learn_paths_loaded: "No learn paths loaded yet",
                }
            },
            ro: {
                translation: {
                    // General
                    true: "Adevărat",
                    false: "Fals",
                    yes: "Da",
                    no: "Nu",
                    correct: "Corect",
                    wrong: "Greșit",
                    open: "Deschide",
                    cancel: "Anulare",
                    save: "Salvare",
                    title: "Titlu",
                    school: "Școală",
                    highschool: "Liceu",
                    university: "Universitate",

                    // Navbar
                    learn_tab: "Învață",
                    quiz_tab: "Quiz",
                    learn_path_tab: "Plan de învățare",
                    sign_in_button: "Conectare",
                    sign_out_button: "Deconectare",

                    // Login
                    login: "Logare",
                    register: "Înregistrare",
                    sign_in: "Conectare",
                    sign_up: "Creare cont",
                    username: "Nume utilizator",
                    email: "Email",
                    password: "Parolă",

                    // Home
                    home_page_title: "Digi Brain Administrare",
                    home_page_description: "Aceasta este pagina de administrare pentru aplicația mobilă Digi Brain. Această pagină este concepută pentru ca administratorii și profesorii să își ușureze munca, având un loc unde pot gestiona toate materialele educaționale, inclusiv lecțiile, întrebările și planurile de învățare.",
                    home_page_motto: "Explorează, învață, reușește: Călătoria ta educațională începe aici",

                    // Learn
                    choose_class: "Alege clasa",
                    select_class: "Selectează clasa",
                    selected_class: "Clasa selectată",
                    no_class_selected: "Nicio clasă selectată",
                    at_university: "La universitate",
                    choose_subject: "Alege un subiect",
                    select_subject: "Selectează subiectul",
                    selected_subject: "Subiectul selectat",
                    no_subject_selected: "Niciun subiect selectat",
                    subject_content: "Conținut subiect",
                    search_filter: "Filtru de căutare",
                    no_subject_loaded: "Niciun subiect deschis încă",

                    // Quiz
                    select_difficulty: "Selectează dificultatea",
                    easy: "Ușor",
                    medium: "Mediu",
                    hard: "Greu",
                    select_question_type: "Selectează tipul de întrebare",
                    multiple_choice: "Răspuns Multiplu",
                    words_gap: "Cuvinte lipsă",
                    true_false: "Adevărat sau Fals",
                    question_text: "Text întrebare",
                    answers: "Răspunsuri",
                    answer: "Răspuns",
                    position: "Poziție",
                    delete_question_check: "Sigur vrei să ștergi această întrebare?",
                    question: "Întrebare",
                    no_questions_loaded: "Nicio întrebare încărcată încă",

                    // Learn Path
                    lessons: "Lecții",
                    quiz: "Quiz",
                    author: "Autor",
                    description: "Descriere",
                    last_updated: "Actualizat ultima oară",
                    started: "Început de",
                    times: "ori",
                    no_learn_paths_loaded: "Niciun plan de învățare încărcat încă",
                }
            }
        }
    });

export default i18n;