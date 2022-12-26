using Microsoft.EntityFrameworkCore.Migrations;

namespace DigiBrainServer.Migrations
{
    public partial class Classtableupdated : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "AtUniversity",
                table: "Class",
                type: "boolean",
                nullable: false,
                defaultValue: false);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "AtUniversity",
                table: "Class");
        }
    }
}
