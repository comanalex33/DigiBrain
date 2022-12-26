using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

namespace DigiBrainServer.Migrations
{
    public partial class DomainandClasstablesadded : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Class",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    number = table.Column<int>(type: "integer", nullable: false),
                    domainId = table.Column<long>(type: "bigint", nullable: false),
                    requestToResolve = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Class", x => x.id);
                });

            migrationBuilder.CreateTable(
                name: "Domain",
                columns: table => new
                {
                    id = table.Column<long>(type: "bigint", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    name = table.Column<string>(type: "text", nullable: true),
                    requestToResolve = table.Column<bool>(type: "boolean", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Domain", x => x.id);
                });
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Class");

            migrationBuilder.DropTable(
                name: "Domain");
        }
    }
}
