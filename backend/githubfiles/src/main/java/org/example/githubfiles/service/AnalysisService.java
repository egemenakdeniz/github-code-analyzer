package org.example.githubfiles.service;

import org.example.githubfiles.repository.*;
import org.example.githubfiles.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnalysisService {

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private RepositoryRepository repositoryRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private OllamaService ollamaService;
    @Autowired
    private PdfReportService pdfReportService;

    public String analyzeRepository(String owner, String repoName, String branch,String modelName) {
        Optional<Repository> repositoryOpt = repositoryRepository.findByUserNameAndRepoNameAndBranchName(owner, repoName, branch);
        if (repositoryOpt.isEmpty()) {
            throw new RuntimeException("Repository not found.");
        }

        Repository repository = repositoryOpt.get();

        List<File> files = fileRepository.findByRepositoryIdAndIsActiveTrue(repository.getId());

        StringBuilder promptBuilder=getPrompt(files);
        System.out.println(promptBuilder.toString());
        Session session = saveSession(repository,modelName,promptBuilder.toString());
        String raw= ollamaService.analyzeWithModel(promptBuilder.toString());

        String fullText = modelMessageConverter(raw);
        //System.out.println("Model cevabı: \n" + fullText);

        List<Result> results = parseResults(fullText,session);
        //System.out.println(a.size());
        resultRepository.saveAll(results);

        try {
            String pdfPath = pdfReportService.generateReportPdf(repository, session, results);
            if (pdfPath != null) {
                Report report = new Report();
                report.setSession(session);
                report.setPath(pdfPath);
                report.setCreated_at(LocalDateTime.now());
                reportRepository.save(report);
            }
        } catch (Exception e) {
            System.err.println("PDF oluşturulamadı: " + e.getMessage());
        }
        return fullText;
    }

    public List<Result> parseResults(String response, Session session) {
        List<Result> results = new ArrayList<>();
        Result current = null;
        String currentFileName = null;

        for (String line : response.split("\n")) {
            if (line.startsWith("FILE:")) {
                current = new Result();
                currentFileName = line.substring(5).trim();
                Optional<File> fileOpt = fileRepository.findByPathAndRepository_IdAndIsActiveTrue(currentFileName, session.getRepository().getId());
                if (fileOpt.isEmpty()) continue;
                current.setFile(fileOpt.get());
            } else if (line.startsWith("CLASS:")) {
                current.setClass_name(line.substring(6).trim());
            } else if (line.startsWith("SEVERITY:")) {
                current.setSeverity(line.substring(9).trim());
            } else if (line.startsWith("ISSUE:")) {
                current.setIssue(line.substring(6).trim());
            } else if (line.startsWith("SUGGESTION:")) {
                current.setSuggestions(line.substring(11).trim());
                current.setSession(session);
                current.setAnalyzed_at(LocalDateTime.now());
                results.add(current);
            }
        }
        return results;
    }

    String modelMessageConverter(String fullText) {
        StringBuilder finalResponse = new StringBuilder();

        String[] lines = fullText.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                JSONObject obj = new JSONObject(line);
                finalResponse.append(obj.getString("response"));
            }
        }
        return finalResponse.toString();
    }

    StringBuilder getPrompt(List<File> files) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You will be given multiple source code files from a software project.\n" +
                "\n" +
                "Your job is NOT to complete, rewrite, or explain the code.  \n" +
                "Your ONLY task is to identify and list potential issues found in each file.  \n" +
                "You MUST use the following strict format for every issue:\n" +
                "\n" +
                "FORMAT:\n" +
                "FILE: <filename>  \n" +
                "CLASS: <class name or \"N/A\">  \n" +
                "SEVERITY: TRIVIAL | MID | CRITICAL \n" +
                "ISSUE: <brief description of the problem>  \n" +
                "SUGGESTION: <how to fix or improve the issue>\n" +
                "\n" +
                "If a file contains multiple issues, repeat the FILE block for each issue.\n" +
                "\n" +
                "Do NOT include any explanations, summaries, extra formatting (like bullet points), or markdown.  \n" +
                "Do NOT output anything outside this format.\n" +
                "\n" +
                "Example output:\n" +
                "\n" +
                "FILE: UserController.java  \n" +
                "CLASS: UserController  \n" +
                "SEVERITY: CRITICAL  \n" +
                "ISSUE: User input is directly added to the SQL query, leading to SQL injection risk.  \n" +
                "SUGGESTION: Use parameterized queries or prepared statements.\n" +
                "\n" +
                "FILE: AuthService.java  \n" +
                "CLASS: AuthService  \n" +
                "SEVERITY: MID  \n" +
                "ISSUE: Passwords are stored without encryption.  \n" +
                "SUGGESTION: Use a secure hashing algorithm like bcrypt for storing passwords.\n" +
                "\n" +
                "---\n" +
                "\n" +
                "Now analyze the following files:\n");

        for (File file : files) {
            promptBuilder.append("--\n");
            promptBuilder.append("### FILE START ###\n");
            promptBuilder.append("filename=\"").append(file.getPath()).append("\"\n");
            promptBuilder.append("content=\"").append(file.getContent().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")).append("\"\n");
            promptBuilder.append("### FILE END ###\n");
        }
        promptBuilder.append("\nIMPORTANT: DO NOT generate or continue the code below. Just analyze it.");
        return promptBuilder;
    }

    Session saveSession(Repository repo,String modelName,String prompt) {
        Session session = new Session();
        session.setRepository(repo);
        session.setPrompt(prompt);
        session.setModel_name(modelName);
        session.setExecuted_at(java.time.LocalDateTime.now());
        sessionRepository.save(session);
        return session;
        }
}
