import os
import shutil

source_base = "/home/msi/projects/cvenhance/doc-service/src/main/java/com/docservice/careerhub"
target_base = "/home/msi/projects/cvenhance/app/doc-service/src/main/java/com/cvenhance/doc"

def process_file(rel_path):
    src = os.path.join(source_base, rel_path)
    tgt = os.path.join(target_base, rel_path)
    os.makedirs(os.path.dirname(tgt), exist_ok=True)
    with open(src, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Package and import changes
    content = content.replace("com.docservice.careerhub", "com.cvenhance.doc")
    
    # Common lib replacements
    content = content.replace("com.cvenhance.doc.exception.ApiException", "com.cvenhance.common.exception.ApiException")
    content = content.replace("com.cvenhance.doc.dto.request.PageQuery", "com.cvenhance.common.dto.PageQuery")
    content = content.replace("com.cvenhance.doc.dto.response.PageResponse", "com.cvenhance.common.dto.PageResponse")
    content = content.replace("com.cvenhance.doc.dto.response.MessageResponse", "com.cvenhance.common.dto.MessageResponse")
    content = content.replace("com.cvenhance.doc.util.PageUtil", "com.cvenhance.common.util.PageUtil")
    content = content.replace("com.cvenhance.doc.audit", "com.cvenhance.common.audit")
    
    # EntitlementService -> SubscriptionServiceClient changes
    if "UserDocService" in rel_path or "ResumeBuilderDocumentService" in rel_path or "UserDocDtoApi" in rel_path:
        content = content.replace("EntitlementService", "SubscriptionServiceClient")
        content = content.replace("entitlementService", "subscriptionServiceClient")
        content = content.replace("import com.cvenhance.doc.service.SubscriptionServiceClient;", "import com.cvenhance.doc.client.SubscriptionServiceClient;")
    
    # Remove AuthService and AccountMailer from ResumeBuilderDocumentService
    if "ResumeBuilderDocumentService" in rel_path:
        content = content.replace("import com.cvenhance.doc.entity.AuthUser;", "")
        content = content.replace("private AccountMailer accountMailer;", "")
        content = content.replace("private AuthService authService;", "")
        content = content.replace("AuthUser user = authService.getActiveUser(ownerEmail);", "")
        content = content.replace("accountMailer.sendTemplateUnlocked(ownerEmail, user.getFullName(), document.getName());", "")
    
    with open(tgt, 'w', encoding='utf-8') as f:
        f.write(content)

# Define exactly which files to copy
files_to_copy = [
    "entity/DocTemplate.java", "entity/UserDoc.java", "entity/ResumeBuilderTemplate.java", "entity/ResumeBuilderDocument.java",
    "repo/DocTemplateRepository.java", "repo/UserDocRepository.java", "repo/ResumeBuilderDocumentRepository.java", "repo/ResumeBuilderTemplateRepository.java",
    "service/DocTemplateService.java", "service/DocTemplateCompiler.java", "service/UserDocService.java",
    "service/ResumeBuilderDocumentService.java", "service/ResumeBuilderTemplateService.java",
    "service/LatexCompiler.java", "service/NativeLatexCompiler.java", "service/DockerTexliveCompiler.java",
    "service/WatermarkService.java", "service/S3StorageService.java", "service/StorageService.java",
    "controller/DocTemplateController.java", "controller/UserDocController.java",
    "controller/ResumeBuilderDocumentController.java", "controller/ResumeBuilderTemplateController.java",
    "dtoApi/DocTemplateDtoApi.java", "dtoApi/UserDocDtoApi.java",
    "dtoApi/ResumeBuilderDocumentDtoApi.java", "dtoApi/ResumeBuilderTemplateDtoApi.java",
    "dto/constants/DocTemplateStatus.java", "dto/constants/DocType.java", "dto/constants/SubscriptionType.java",
    "dto/request/CreateDocTemplateRequest.java", "dto/request/CompileDocRequest.java",
    "dto/request/SaveUserDocRequest.java", "dto/request/SaveResumeBuilderDocumentRequest.java",
    "dto/request/UpsertResumeBuilderTemplateRequest.java",
    "dto/request/AdminDocTemplateUpdate.java", "dto/request/AdminUserDocUpdate.java",
    "dto/response/DocTemplateMetadata.java", "dto/response/UserDocMetadata.java", "dto/response/UserDocResponse.java",
    "dto/response/ResumeBuilderDocumentResponse.java", "dto/response/ResumeBuilderTemplateResponse.java",
    "dto/response/AdminUserDocDto.java",
    "config/SchedulingConfig.java", "config/AppProperties.java",
    "util/AbstractDtoUtil.java"
]

for f in files_to_copy:
    try:
        process_file(f)
    except Exception as e:
        print(f"Error processing {f}: {e}")

# Application class
app_class = """package com.cvenhance.doc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DocServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocServiceApplication.class, args);
    }
}
"""
with open(os.path.join(target_base, "DocServiceApplication.java"), "w") as f:
    f.write(app_class)

print("Migration completed.")
