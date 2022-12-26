using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using DigiBrainServer.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.Controllers
{
    [Route("api/domains")]
    [ApiController]
    public class DomainController : ControllerBase
    {
        private readonly AppDbContext _context;

        public DomainController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<DomainResponseModel>> GetById(long id)
        {
            var domain = await _context.Domain.FindAsync(id);
            if(domain == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This domain does not exist"
                });
            }

            return Ok((DomainResponseModel)domain);
        }

        [HttpPost]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<DomainResponseModel>> AddDomain(DomainViewModel domainModel)
        {
            var domainCheck = _context.Domain.Where(item => item.Name.Equals(domainModel.Name)).FirstOrDefault();
            if (domainCheck != null)
            {
                return BadRequest(new { message = "Domain already exists" });
            }

            long id = _context.Domain.Count() + 1;
            domainCheck = await _context.Domain.FindAsync(id);
            while(domainCheck != null)
            {
                id++;
                domainCheck = await _context.Domain.FindAsync(id);
            }

            DomainModel domain = new(id, domainModel.Name, domainModel.LanguageId, domainModel.IconId);
            _context.Domain.Add(domain);
            await _context.SaveChangesAsync();

            return Ok((DomainResponseModel)domain);
        }

        [HttpDelete("{id}")]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<DomainResponseModel>> DeleteDomain(long id)
        {
            var domain = await _context.Domain.FindAsync(id);
            if(domain == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This domain does not exist"
                });
            }

            _context.Domain.Remove(domain);
            await _context.SaveChangesAsync();

            return Ok((DomainResponseModel)domain);
        }
    }
}
