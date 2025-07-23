package org.example.githubfiles.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.githubfiles.repository.*;
import org.example.githubfiles.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnalysisService {

    @Value("${ai.default.model}")
    private String defaultModel;

    private final ReportRepository reportRepository;
    private final ResultRepository resultRepository;
    private final SessionRepository sessionRepository;
    private final RepositoryRepository repositoryRepository;
    private final FileRepository fileRepository;
    private final PdfReportService pdfReportService;

    private final AiService aiService;

    public void analyzeRepository(Repository repositoryInfo, String providerName,String modelName) {
        Optional<Repository> repositoryOpt = repositoryRepository.findByUserNameAndRepoNameAndBranchName(
                repositoryInfo.getUserName(),
                repositoryInfo.getRepoName(),
                repositoryInfo.getBranchName()
        );
        log.info("Gelen provider: {}", providerName);
        log.info("Gelen model: {}", modelName);
        Repository repository = repositoryOpt.get();
        List<File> files = fileRepository.findByRepositoryIdAndIsActiveTrue(repository.getId());

        String result="";
        if ("ollama".equalsIgnoreCase(providerName)) {
            for (File file : files) {
                StringBuilder prmt= getPromtStart();
                prmt.append("filename=\"").append(file.getPath()).append("\"\n");
                prmt.append("content:\n");
                prmt.append(file.getContent()).append("\n");

                log.info(file.getPath());
                log.info("Model Name : "+modelName);
                String modelResult = aiService.ask("ollama",modelName,prmt.toString().replace("\t", "    "));
                log.info(modelResult);
                result = result + modelResult;
            }
        }

        else if ("openai".equalsIgnoreCase(providerName)) {
            for (File file : files) {
                StringBuilder prmt= getPromtStart();
                prmt.append("filename=\"").append(file.getPath()).append("\"\n");
                prmt.append("content:\n");
                prmt.append(file.getContent()).append("\n");

                log.debug(file.getPath());
                String modelResult = aiService.ask("openai",modelName,prmt.toString().replace("\t", "    "));
                log.debug(modelResult);
                result = result + modelResult;
            }
        }else {
            for (File file : files) {
                StringBuilder prmt= getPromtStart();

                prmt.append("filename=\"").append(file.getPath()).append("\"\n");
                prmt.append("content:\n");
                prmt.append(file.getContent()).append("\n");

                log.debug(file.getPath());
                String modelResult = aiService.ask(null,modelName,prmt.toString().replace("\t", "    "));
                log.debug(modelResult);
                result = result + modelResult;
            }
        }
        if(modelName==null){modelName=defaultModel;}
        Session session = saveSession(repository, modelName, getPromtStart().toString());

        List<Result> results = parseResults(result.trim(), session);
        resultRepository.saveAll(results);

        try {
            byte[] pdf = pdfReportService.generateReportPdfBytes(repository, results);
            log.debug("PDF length: " + pdf.length);

            if (pdf != null) {
                reportRepository.insertReport(LocalDateTime.now(),
                        pdf,
                       session.getId());
            }
        } catch (Exception e) {
                //pdf oluşturulamadı fırlat
        }
        //return "SONUC: "+result;
    }

    private StringBuilder getPromtStart(){
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You will be given a single source code file from a software project.\n" +
                "\n" +
                "Your job is NOT to complete, rewrite, or explain the code.  \n" +
                "Your ONLY task is to identify and list potential issues found in the file.  \n" +
                "You MUST use the following strict format for every issue:\n" +
                "\n" +
                "FORMAT:\n" +
                "FILE: <file path>  \n" +
                "CLASS: <class name or \"N/A\">  \n" +
                "SEVERITY: TRIVIAL | MID | CRITICAL  \n" +
                "ISSUE: <brief description of the problem>  \n" +
                "SUGGESTION: <how to fix or improve the issue>\n" +
                "\n" +
                "If the file contains multiple issues, repeat the FILE block for each issue.\n" +
                "\n" +
                "Do NOT include any explanations, summaries, extra formatting (like bullet points), or markdown.  \n" +
                "Do NOT output anything outside this format.\n" +
                "\n" +
                "Example output:\n" +
                "\n" +
                "FILE: backend/githubfiles/src/main/java/org/example/githubfiles/model/Report.java  \n" +
                "CLASS: UserController  \n" +
                "SEVERITY: CRITICAL  \n" +
                "ISSUE: User input is directly added to the SQL query, leading to SQL injection risk.  \n" +
                "SUGGESTION: Use parameterized queries or prepared statements.\n" +
                "\n" +
                "FILE: C/DynamicMemory.c  \n" +
                "CLASS: AuthService  \n" +
                "SEVERITY: MID  \n" +
                "ISSUE: Passwords are stored without encryption.  \n" +
                "SUGGESTION: Use a secure hashing algorithm like bcrypt for storing passwords.\n" +
                "\n" +
                "---\n" +
                "\n" +
                "Now analyze the following file:");
        return promptBuilder;
    }

    public List<Result> parseResults(String response, Session session) {
        List<Result> results = new ArrayList<>();
        Result current = null;
        String currentFileName = null;
        for (String line : response.split("\n")) {
            if (line.startsWith("FILE:")) {
                current = new Result();
                currentFileName = line.substring(5).trim();
                System.out.println(currentFileName);
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
