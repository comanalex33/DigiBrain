using Microsoft.EntityFrameworkCore.Migrations;

namespace DigiBrainServer.Migrations
{
    public partial class Usersrolechange : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "RoleRequest",
                table: "AspNetUsers",
                type: "text",
                nullable: true);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "RoleRequest",
                table: "AspNetUsers");
        }
    }
}
