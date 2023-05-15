using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using DigiBrainServer.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.Controllers
{
    [Route("api/classes")]
    [ApiController]
    public class ClassController : ControllerBase
    {
        private readonly AppDbContext _context;

        public ClassController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<ClassResponseModel>> GetClassById(long id)
        {
            var classModel = await _context.Class.FindAsync(id);
            if (classModel == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This class does not exists"
                });
            }
            return Ok((ClassResponseModel)classModel);
        }

        [HttpGet]
        [Route("domains")]
        public async Task<ActionResult<IEnumerable<DomainResponseModel>>> GetDomainsByClassNumber([FromQuery]ClassDomainsViewModel classDomainModel)
        {
            var classes = await _context.Class.Where(item => item.Number == classDomainModel.Number && item.AtUniversity == classDomainModel.AtUniversity).ToListAsync();
            if(classes == null || classes.Count == 0)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Classes not found for this number and at University"
                });
            }

            var domains = new List<DomainResponseModel>();
            foreach(var classModel in classes) {
                var domain = await _context.Domain.FindAsync(classModel.DomainId);
                if(domain == null)
                {
                    return NotFound(new ErrorResponseModel
                    {
                        Message = "Domain not found"
                    });
                }
                if(domain.LanguageId == classDomainModel.LanguageId)
                {
                    domains.Add((DomainResponseModel)domain);
                }   
            }

            return Ok(domains);
        }

        [HttpGet]
        [Route("all")]
        public async Task<ActionResult<IEnumerable<ClassResponseModel>>> GetClasses()
        {
            var classes = await _context.Class.ToListAsync();

            var responseClasses = new List<ClassResponseModel>();
            foreach(var classModel in classes)
            {
                responseClasses.Add((ClassResponseModel)classModel);
            }

            return Ok(responseClasses);
        }

        [HttpGet]
        public async Task<ActionResult<ClassResponseModel>> GetClassByNumberAndDomain([FromQuery] ClassViewModel classModel)
        {
            var classes = await _context.Class.Where(item => item.Number == classModel.Number && item.AtUniversity == classModel.AtUniversity && item.DomainId == classModel.DomainId).ToListAsync();
            if (classes == null || classes.Count == 0)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "Classes not found for this number and domain"
                });
            }
            if (classes.Count > 1)
            {
                return BadRequest(new ErrorResponseModel
                {
                    Message = "Classes found multiple times"
                });
            }

            return Ok((ClassResponseModel)classes[0]);

        }

        [HttpPost]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<ClassResponseModel>> AddClass(ClassViewModel classModel)
        {
            var classCheck = _context.Class.Where(item => item.Number.Equals(classModel.Number) && item.AtUniversity == classModel.AtUniversity).FirstOrDefault();
            if (classCheck != null)
            {
                return BadRequest(new { message = "Class already exists" });
            }

            long id = _context.Class.Count() + 1;
            classCheck = await _context.Class.FindAsync(id);
            while (classCheck != null)
            {
                id++;
                classCheck = await _context.Class.FindAsync(id);
            }

            var domainCheck = await _context.Domain.FindAsync(classModel.DomainId);
            if (domainCheck == null)
            {
                return BadRequest(new { message = "Domain does not exists" });
            }

            ClassModel classModelToSave = new(id, classModel.Number, classModel.DomainId, classModel.AtUniversity);
            _context.Class.Add(classModelToSave);
            await _context.SaveChangesAsync();

            return Ok((ClassResponseModel)classModelToSave);
        }

        [HttpDelete("{id}")]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<ClassResponseModel>> DeleteClass(long id)
        {
            var classModel = await _context.Class.FindAsync(id);
            if (classModel == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This class does not exist"
                });
            }

            _context.Class.Remove(classModel);
            await _context.SaveChangesAsync();

            return Ok((ClassResponseModel)classModel);
        }
    }
}
