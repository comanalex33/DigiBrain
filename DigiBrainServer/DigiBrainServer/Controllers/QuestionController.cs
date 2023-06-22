using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using DigiBrainServer.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.Controllers
{
    [Route("api/questions")]
    [ApiController]
    public class QuestionController : ControllerBase
    {
        private readonly AppDbContext _context;
        private readonly List<string> acceptedTypes = new()
        {
            "MultipleChoice",
            "WordsGap",
            "TrueFalse",
            "Any"
        };
        private readonly List<string> acceptedDifficulties = new()
        {
            "Easy",
            "Medium",
            "Hard",
            "Any"
        };
        public QuestionController(AppDbContext context)
        {
            _context = context;
        }

        [HttpPost]
        [Route("ids")]
        public async Task<ActionResult<IEnumerable<QuestionResponseModel>>> GetQuestionsForIds(List<long> questionIds)
        {
            var responseQuestions = new List<QuestionResponseModel>();
            foreach (var questionId in questionIds)
            {
                var question = await _context.Question.FindAsync(questionId);
                if (question != null)
                {
                    responseQuestions.Add((QuestionResponseModel)question);
                }
            }

            return Ok(responseQuestions);
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<QuestionResponseModel>>> GetQuestions([FromQuery]QuestionGeneratorViewModel model)
        {
            // Check type correctness
            if(!acceptedTypes.Contains(model.Type))
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Type not allowed, accepted types are " + string.Join(",", acceptedTypes)
                });
            }
            // Check difficulty correctness
            if (!acceptedDifficulties.Contains(model.Difficulty))
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Difficulty not allowed, accepted difficulties are " + string.Join(",", acceptedDifficulties)
                });
            }

            if (model.Number > 0)
            {
                if (GetNumberOfValidQuestions(model.Difficulty, model.Type, model.LanguageId) < model.Number)
                {
                    return NotFound(new ErrorResponseModel
                    {
                        Message = "Doesn't exist so much questions in the databse"
                    });
                }
            }

            var questions = new List<QuestionResponseModel>();
            
            if (model.Number > 0)
            {
                var selectedQuestionsIds = new List<long>();

                // Get questions Ids
                var questionsIds = await _context.Question.Select(item => item.Id).ToListAsync();

                Random rnd = new();
                for (int number = 1; number <= model.Number; number++)
                {
                    while (true)
                    {
                        // Generate questionId
                        var questionIdPosition = rnd.Next(0, questionsIds.Count);
                        if (selectedQuestionsIds.Contains(questionIdPosition))
                        {
                            continue;
                        }
                        var questionId = questionsIds[questionIdPosition];

                        // Get and check question
                        var question = await _context.Question.FindAsync(questionId);
                        if (model.Type.Equals("Any") || question.Type.Equals(model.Type))
                        {
                            if (model.Difficulty.Equals("Any") || question.Difficulty.Equals(model.Difficulty))
                            {
                                if (question.LanguageId == model.LanguageId)
                                {
                                    selectedQuestionsIds.Add(questionIdPosition);
                                    questions.Add((QuestionResponseModel)question);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else
            {
                var allQuestions = await _context.Question.ToListAsync();
                foreach(var question in allQuestions)
                {
                    if (model.Type.Equals("Any") || question.Type.Equals(model.Type))
                    {
                        if (model.Difficulty.Equals("Any") || question.Difficulty.Equals(model.Difficulty))
                        {
                            if (question.LanguageId == model.LanguageId)
                            {
                                questions.Add((QuestionResponseModel)question);
                            }
                        }
                    }
                }
            }

            return Ok(questions);
        }

        [HttpGet]
        [Route("subject/{id}")]
        public async Task<ActionResult<IEnumerable<QuestionResponseModel>>> GetQuestionsForSubject(long id, [FromQuery] QuestionGeneratorViewModel model)
        {
            // Check type correctness
            if (!acceptedTypes.Contains(model.Type))
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Type not allowed, accepted types are " + string.Join(",", acceptedTypes)
                });
            }
            // Check difficulty correctness
            if (!acceptedDifficulties.Contains(model.Difficulty))
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Difficulty not allowed, accepted difficulties are " + string.Join(",", acceptedDifficulties)
                });
            }
            if (model.Number > 0)
            {
                if (GetNumberOfValidQuestionsForSubject(id, model.Difficulty, model.Type, model.LanguageId) < model.Number)
                {
                    return NotFound(new ErrorResponseModel
                    {
                        Message = "Doesn't exist so many questions in the databse"
                    });
                }
            }

            var questions = new List<QuestionResponseModel>();


            var selectedQuestionsIds = new List<long>();

            if (model.Number > 0)
            {
                // Get questions Ids
                var questionsIds = await _context.Question.Select(item => item.Id).ToListAsync();

                Random rnd = new();
                for (int number = 1; number <= model.Number; number++)
                {
                    while (true)
                    {
                        // Generate questionId
                        var questionIdPosition = rnd.Next(0, questionsIds.Count);
                        if (selectedQuestionsIds.Contains(questionIdPosition))
                        {
                            continue;
                        }
                        var questionId = questionsIds[questionIdPosition];
                        if (!ValidQuestionForSubject(id, questionId))
                        {
                            continue;
                        }

                        // Get and check question
                        var question = await _context.Question.FindAsync(questionId);
                        if (model.Type.Equals("Any") || question.Type.Equals(model.Type))
                        {
                            if (model.Difficulty.Equals("Any") || question.Difficulty.Equals(model.Difficulty))
                            {
                                if (question.LanguageId == model.LanguageId)
                                {
                                    selectedQuestionsIds.Add(questionIdPosition);
                                    questions.Add((QuestionResponseModel)question);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else
            {
                var allQuestions = await _context.Question.ToListAsync();
                foreach (var question in allQuestions)
                {
                    if (ValidQuestionForSubject(id, question.Id))
                    {
                        if (model.Type.Equals("Any") || question.Type.Equals(model.Type))
                        {
                            if (model.Difficulty.Equals("Any") || question.Difficulty.Equals(model.Difficulty))
                            {
                                if (question.LanguageId == model.LanguageId)
                                {
                                    questions.Add((QuestionResponseModel)question);
                                }
                            }
                        }
                    }
                }
            }

            return Ok(questions);
        }

        [HttpGet]
        [Route("subject/{id}/answers")]
        public async Task<ActionResult<IEnumerable<QuestionResponseModel>>> GetQuestionsWithAnswersForSubject (long id, [FromQuery] QuestionGeneratorViewModel model)
        {
            // Check type correctness
            if (!acceptedTypes.Contains(model.Type))
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Type not allowed, accepted types are " + string.Join(",", acceptedTypes)
                });
            }
            // Check difficulty correctness
            if (!acceptedDifficulties.Contains(model.Difficulty))
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Difficulty not allowed, accepted difficulties are " + string.Join(",", acceptedDifficulties)
                });
            }
            if (model.Number > 0)
            {
                if (GetNumberOfValidQuestionsForSubject(id, model.Difficulty, model.Type, model.LanguageId) < model.Number)
                {
                    return NotFound(new ErrorResponseModel
                    {
                        Message = "Doesn't exist so many questions in the databse"
                    });
                }
            }

            var questions = new List<QuestionsAnswersResponseModel>();


            var selectedQuestionsIds = new List<long>();

            if (model.Number > 0)
            {
                // Get questions Ids
                var questionsIds = await _context.Question.Select(item => item.Id).ToListAsync();

                Random rnd = new();
                for (int number = 1; number <= model.Number; number++)
                {
                    while (true)
                    {
                        // Generate questionId
                        var questionIdPosition = rnd.Next(0, questionsIds.Count);
                        if (selectedQuestionsIds.Contains(questionIdPosition))
                        {
                            continue;
                        }
                        var questionId = questionsIds[questionIdPosition];
                        if (!ValidQuestionForSubject(id, questionId))
                        {
                            continue;
                        }

                        // Get and check question
                        var question = await _context.Question.FindAsync(questionId);
                        if (model.Type.Equals("Any") || question.Type.Equals(model.Type))
                        {
                            if (model.Difficulty.Equals("Any") || question.Difficulty.Equals(model.Difficulty))
                            {
                                if (question.LanguageId == model.LanguageId)
                                {
                                    selectedQuestionsIds.Add(questionIdPosition);

                                    var answers = await _context.Answer.Where(item => item.QuestionId == question.Id).ToListAsync();
                                    var responseAnswers = new List<AnswerResponseModel>();
                                    foreach (var answer in answers)
                                    {
                                        responseAnswers.Add((AnswerResponseModel)answer);
                                    }

                                    var questionAnswers = new QuestionsAnswersResponseModel(question.Id, question.Type, question.Difficulty, question.Text, question.LanguageId, responseAnswers);
                                    questions.Add(questionAnswers);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                var allQuestions = await _context.Question.ToListAsync();
                foreach (var question in allQuestions)
                {
                    if (ValidQuestionForSubject(id, question.Id))
                    {
                        if (model.Type.Equals("Any") || question.Type.Equals(model.Type))
                        {
                            if (model.Difficulty.Equals("Any") || question.Difficulty.Equals(model.Difficulty))
                            {
                                if (question.LanguageId == model.LanguageId)
                                {
                                    var answers = await _context.Answer.Where(item => item.QuestionId == question.Id).ToListAsync();
                                    var responseAnswers = new List<AnswerResponseModel>();
                                    foreach (var answer in answers)
                                    {
                                        responseAnswers.Add((AnswerResponseModel)answer);
                                    }

                                    var questionAnswers = new QuestionsAnswersResponseModel(question.Id, question.Type, question.Difficulty, question.Text, question.LanguageId, responseAnswers);
                                    questions.Add(questionAnswers);
                                }
                            }
                        }
                    }
                }
            }

            return Ok(questions);
        }

        [HttpPost]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<QuestionResponseModel>> AddQuestion(QuestionViewModel model)
        {
            // Check type correctness
            if (!acceptedTypes.Contains(model.Type))
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Type not allowed, accepted types are " + string.Join(",", acceptedTypes)
                });
            }
            // Check difficulty correctness
            if (!acceptedDifficulties.Contains(model.Difficulty))
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Difficulty not allowed, accepted difficulties are " + string.Join(",", acceptedDifficulties)
                });
            }

            var questionCheck = _context.Question.Where(item => item.Text == model.Text && item.Difficulty.Equals(model.Difficulty) && item.Type.Equals(model.Type)).FirstOrDefault();
            if (questionCheck != null)
            {
                return BadRequest(new { message = "A Question with this text, difficulty and type already exists" });
            }

            long id = _context.Question.Count() + 1;
            questionCheck = await _context.Question.FindAsync(id);
            while (questionCheck != null)
            {
                id++;
                questionCheck = await _context.Question.FindAsync(id);
            }

            QuestionModel question = new(id, model.Type, model.Difficulty, model.Text, model.LanguageId);
            _context.Question.Add(question);
            await _context.SaveChangesAsync();

            return Ok((QuestionResponseModel)question);
        }

        [HttpPost]
        [Route("{id}/subjects")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<List<SubjectQuestionResponseModel>>> AddQuestionToSubjects(long id, List<long> subjectIds)
        {
            var addedSubjectQuestions = new List<SubjectQuestionResponseModel>();
            foreach(var subjectId in subjectIds)
            {
                var subject = await _context.Subject.FindAsync(subjectId);
                if (subject == null)
                {
                    return NotFound(new ErrorResponseModel
                    {
                        Message = "This subject does not exist"
                    });
                }

                var subjectCheck = _context.LessonQuestion.Where(item => item.QuestionId == id && item.SubjectId == subjectId).FirstOrDefault();
                if (subjectCheck != null)
                {
                    return BadRequest(new { message = "Question already added to this subject" });
                }

                long newId = _context.LessonQuestion.Count() + 1;
                subjectCheck = await _context.LessonQuestion.FindAsync(newId);
                while (subjectCheck != null)
                {
                    newId++;
                    subjectCheck = await _context.LessonQuestion.FindAsync(newId);
                }

                SubjectQuestionModel model = new(newId, subjectId, id);
                _context.LessonQuestion.Add(model);
                addedSubjectQuestions.Add((SubjectQuestionResponseModel)model);
            }

            await _context.SaveChangesAsync();
            return Ok(addedSubjectQuestions);
        }

        [HttpDelete("{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<QuestionResponseModel>> DeleteQuestion(long id)
        {
            var question = await _context.Question.FindAsync(id);
            if (question == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This question does not exist"
                });
            }

            _context.Question.Remove(question);

            var answers = await _context.Answer.Where(item => item.QuestionId == question.Id).ToListAsync();
            foreach(var answer in answers)
            {
                _context.Answer.Remove(answer);
            }

            await _context.SaveChangesAsync();

            return Ok((QuestionResponseModel)question);
        }

        [HttpPut("{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<QuestionResponseModel>> UpdateQuestion(long id, QuestionAnswersViewModel model)
        {
            // Question Update
            var question = await _context.Question.FindAsync(id);
            if (question == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This question does not exist"
                });
            }

            // Check type correctness
            if (!acceptedTypes.Contains(model.Type))
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Type not allowed, accepted types are " + string.Join(",", acceptedTypes)
                });
            }
            // Check difficulty correctness
            if (!acceptedDifficulties.Contains(model.Difficulty))
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Difficulty not allowed, accepted difficulties are " + string.Join(",", acceptedDifficulties)
                });
            }

            var questionCheck = _context.Question.Where(item => item.Id != id && item.Text == model.Text && item.Difficulty.Equals(model.Difficulty) && item.Type.Equals(model.Type)).FirstOrDefault();
            if (questionCheck != null)
            {
                return BadRequest(new { message = "A Question with this text, difficulty and type already exists" });
            }

            question.Text = model.Text;
            question.Difficulty = model.Difficulty;
            question.Type = model.Type;
            question.LanguageId = model.LanguageId;

            _context.Question.Update(question);
            await _context.SaveChangesAsync();

            // Answers remove
            var answers = await _context.Answer.Where(item => item.QuestionId == question.Id).ToListAsync();
            foreach (var answer in answers)
            {
                var idFound = false;
                foreach(var targetAnswer in model.Answers)
                {
                    if (targetAnswer.Id == answer.Id)
                        idFound = true;
                }
                if(!idFound)
                {
                    _context.Answer.Remove(answer);
                }
            }

            await _context.SaveChangesAsync();

            // Answers update
            foreach (var answer in model.Answers)
            {
                if(answer.Id == 0)
                {
                    var answerCheck = _context.Answer.Where(item => item.Text == answer.Text && item.QuestionId == answer.QuestionId).FirstOrDefault();
                    if (answerCheck != null)
                    {
                        return BadRequest(new
                        {
                            message = "An answer with this text already exists for this question: " + answer.QuestionId.ToString()
                        });
                    }

                    long newId = _context.Answer.Count() + 1;
                    answerCheck = await _context.Answer.FindAsync(newId);
                    while (answerCheck != null)
                    {
                        newId++;
                        answerCheck = await _context.Answer.FindAsync(newId);
                    }

                    AnswerModel newAnswer = new(newId, answer.Text, answer.Position, answer.Correct, answer.QuestionId);
                    _context.Answer.Add(newAnswer);
                } else
                {
                    var answerDb = await _context.Answer.FindAsync(answer.Id);
                    if (answerDb == null)
                    {
                        return NotFound(new ErrorResponseModel
                        {
                            Message = "This answer does not exist"
                        });
                    }

                    var answerCheck = _context.Answer.Where(item => item.Id != answer.Id && item.Text == answer.Text && item.QuestionId == answer.QuestionId).FirstOrDefault();
                    if (answerCheck != null)
                    {
                        return BadRequest(new { message = "An answer with this text already exists for this question" });
                    }

                    answerDb.Text = answer.Text;
                    answerDb.Position = answer.Position;
                    answerDb.Correct = answer.Correct;
                    answerDb.QuestionId = answer.QuestionId;

                    _context.Answer.Update(answerDb);
                }
            }
            await _context.SaveChangesAsync();

            return Ok((QuestionResponseModel)question);
        }

        private int GetNumberOfValidQuestions(string Difficulty, string Type, long LanguageId)
        {

            if(Type.Equals("Any") && !Difficulty.Equals("Any"))
            {
                return _context.Question.Where(item => item.Difficulty.Equals(Difficulty) && item.LanguageId == LanguageId).Count();
            }
            if (!Type.Equals("Any") && Difficulty.Equals("Any"))
            {
                return _context.Question.Where(item => item.Type.Equals(Type) && item.LanguageId == LanguageId).Count();
            }
            if (Type.Equals("Any") && Difficulty.Equals("Any"))
            {
                return _context.Question.Where(item => item.LanguageId == LanguageId).Count();
            }
            return _context.Question.Where(item => item.Difficulty.Equals(Difficulty) && item.Type.Equals(Type) && item.LanguageId == LanguageId).Count();
        }

        private int GetNumberOfValidQuestionsForSubject(long id, string Difficulty, string Type, long LanguageId)
        {
            List<QuestionModel> questions;
            if (Type.Equals("Any") && !Difficulty.Equals("Any"))
            {
                questions = _context.Question.Where(item => item.Difficulty.Equals(Difficulty) && item.LanguageId == LanguageId).ToList();
            } else if (!Type.Equals("Any") && Difficulty.Equals("Any"))
            {
                questions = _context.Question.Where(item => item.Type.Equals(Type) && item.LanguageId == LanguageId).ToList();
            } else if (Type.Equals("Any") && Difficulty.Equals("Any"))
            {
                questions = _context.Question.Where(item => item.LanguageId == LanguageId).ToList();
            } else
            {
                questions = _context.Question.Where(item => item.Difficulty.Equals(Difficulty) && item.LanguageId == LanguageId).ToList();
            }

            var subjectQuestions = new List<SubjectQuestionModel>();
            foreach(var question in questions)
            {
                var modelFound = _context.LessonQuestion.Where(item => item.QuestionId == question.Id).ToList();
                foreach(var model in modelFound)
                {
                    subjectQuestions.Add(model);
                }
            }

            int validQuestions = 0;
            foreach(var subjectQuestion in subjectQuestions)
            {
                if (subjectQuestion.SubjectId == id)
                    validQuestions++;
            }

            return validQuestions;
        }

        private bool ValidQuestionForSubject(long subjectId, long questionId)
        {
            var question = _context.Question.Find(questionId);

            var subjectQuestions = _context.LessonQuestion.Where(item => item.QuestionId == questionId).ToList();
            foreach (var subjectQuestion in subjectQuestions)
            {
                if (subjectQuestion.SubjectId == subjectId)
                    return true;
            }

            return false;
        }
    }
}
