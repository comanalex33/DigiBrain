using Microsoft.EntityFrameworkCore.Migrations;

namespace DigiBrainServer.Migrations
{
    public partial class Addedregistrationtokentouser : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "RegistrationToken",
                table: "AspNetUsers",
                type: "text",
                nullable: true);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "RegistrationToken",
                table: "AspNetUsers");
        }
    }
}
