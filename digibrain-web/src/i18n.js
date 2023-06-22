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
                    chapter: "Chapter",
                    lesson: "Lesson",

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
                    requests_tab: "Requests",
                    messages_tab: "Messages",
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
                    delete_chapter_check: "Are you sure you want to delete this chapter?",
                    delete_lesson_check: "Are you sure you want to delete this lesson?",

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
                    delete_section_check: "Are you sure you want to delete this section?",
                    delete_lesson_check: "Are you sure you want to delete this lesson?",
                    delete_theory_check: "Are you sure you want to delete this theory material?",
                    delete_quiz_check: "Are you sure you want to delete this quiz?",
                    new_learn_path_notification_title: "New learn path, subject",
                    new_learn_path_message_start: "A new learn path was just created by",
                    new_learn_path_message_end: "check it out in the app",

                    // Requests
                    admin_approve_check: "Are you sure you want to approve admin role request for user",
                    admin_decline_check: "Are you sure you want to decline admin role request for user",
                    teacher_approve_check: "Are you sure you want to approve teacher role request for user",
                    teacher_decline_check: "Are you sure you want to decline teacher role request for user",
                    role_change: "Role Change",
                    teacher: "Teacher",
                    admin: "Admin",
                    no_teacher_request: "No teacher request",
                    no_admin_request: "No admin request",
                    accepted: "accepted",
                    rejected: "rejected",
                    notify_message_1: "Your",
                    notify_message_2: "request was",
                    by: "by",

                    // Message
                    title_and_message_not_empty: "Title and message cannot be empty",
                    empty_destination: "The message must have a destination",
                    all_users_notified: "All Users notified",
                    students: "Students",
                    teachers: "Teachers",
                    admins: "Admins",
                    notified: "notified",
                    message: "Message",
                    send: "Send",
                    to: "To"
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
                    chapter: "Capitol",
                    lesson: "Lecție",

                    // Navbar
                    learn_tab: "Învață",
                    quiz_tab: "Quiz",
                    learn_path_tab: "Plan de învățare",
                    requests_tab: "Request-uri",
                    messages_tab: "Mesaje",
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
                    delete_chapter_check: "Sigur vrei să ștergi acest capitol?",
                    delete_lesson_check: "Sigur vrei să ștergi această lecție?",

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
                    delete_section_check: "Sigur vrei să ștergi această secțiune?",
                    delete_lesson_check: "Sigur vrei să ștergi această lecție?",
                    delete_theory_check: "Sigur vrei să ștergi aceast material teoretic?",
                    delete_quiz_check: "Sigur vrei să ștergi aceast quiz?",
                    new_learn_path_notification_title: "Plan de învățare nou, subiect",
                    new_learn_path_message_start: "Un nou plan de învățare a fost creat de catre",
                    new_learn_path_message_end: "uită-te peste el în aplicație",

                    // Requests
                    admin_approve_check: "Sigur vrei sa aprobi cererea de admin pentru utilizatorul",
                    admin_decline_check: "Sigur vrei sa refuzi cererea de admin pentru utilizatorul",
                    teacher_approve_check: "Sigur vrei sa aprobi cererea de profesor pentru utilizatorul",
                    teacher_decline_check: "Sigur vrei sa refuzi cererea de profesor pentru utilizatorul",
                    role_change: "Schimbare de rol",
                    teacher: "Profesor",
                    admin: "Admin",
                    no_teacher_request: "Niciun request de profesor",
                    no_admin_request: "Niciun request de admin",
                    accepted: "acceptată",
                    rejected: "respinsă",
                    notify_message_1: "Cererea ta de",
                    notify_message_2: "a fost",
                    by: "de către",

                    // Message
                    title_and_message_not_empty: "Titlul și mesajul nu pot fi goale",
                    empty_destination: "Mesajul trebuie să aibă o destinație",
                    all_users_notified: "Toți utilizatorii au fost notificați",
                    students: "Studenți",
                    teachers: "Profesori",
                    admins: "Admini",
                    notified: "notificați",
                    message: "Mesaj",
                    send: "Trimite",
                    to: "Către"
                }
            }
        }
    });

export default i18n;