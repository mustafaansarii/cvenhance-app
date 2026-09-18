import axios from 'axios';

const api = axios.create({
    baseURL: '/careerhub/api/',
    withCredentials: true,
    headers: { 'Content-Type': 'application/json' },
});

const FORM_TEMPLATES = 'form-templates';
const LATEX_TEMPLATES = 'admin/doc-templates';
const USER_DOCS = 'user-docs';

function listParams({ type, keyword, page = 0, size = 10 }) {
    const params = { page, size };
    if (type && type !== 'ALL') params.type = type;
    if (keyword && keyword.trim()) params.keyword = keyword.trim();
    return params;
}

class DocService {
    // Form-based template catalogue (form_templates table)
    async listFormTemplates(opts) {
        const res = await api.get(FORM_TEMPLATES, { params: listParams(opts) });
        return res.data;
    }

    // LaTeX template catalogue (doc_templates table, admin-seeded)
    async listLatexTemplates(opts) {
        const res = await api.get(LATEX_TEMPLATES, { params: listParams(opts) });
        return res.data;
    }

    async listUserDocs(opts) {
        const res = await api.get(USER_DOCS, { params: listParams(opts) });
        return res.data;
    }

    async getUserDoc(id) {
        const res = await api.get(`${USER_DOCS}/${id}`);
        return res.data;
    }

    async saveTemplateToAccount(templateId) {
        const res = await api.post(USER_DOCS, { templateId });
        return res.data;
    }

    async openByTemplate(code) {
        const res = await api.post(`${USER_DOCS}/by-template/${code}`);
        return res.data;
    }

    async claim(id) {
        await api.post(`${USER_DOCS}/${id}/claim`);
    }

    async compile(id, latexCode) {
        const res = await api.patch(
            `${USER_DOCS}/${id}/compile`,
            { latexCode },
            { responseType: 'blob' },
        );
        return res.data;
    }
}

export default new DocService();
