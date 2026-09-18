import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: '/careerhub/api/internal/',
    withCredentials: true,
    headers: { 'Content-Type': 'application/json' },
});

// params: { page, size, keyword, sortBy, direction }
class InternalService {
    async listUsers(params) {
        const res = await axiosInstance.get('users', { params });
        return res.data;
    }

    async updateUsers(updates) {
        const res = await axiosInstance.patch('users', updates);
        return res.data;
    }

    async listTemplates(params) {
        const res = await axiosInstance.get('templates', { params });
        return res.data;
    }

    async updateTemplates(updates) {
        const res = await axiosInstance.patch('templates', updates);
        return res.data;
    }

    async listUserDocs(params) {
        const res = await axiosInstance.get('user-docs', { params });
        return res.data;
    }

    async updateUserDocs(updates) {
        const res = await axiosInstance.patch('user-docs', updates);
        return res.data;
    }

    async listAudit(params) {
        const res = await axiosInstance.get('audit-events', { params });
        return res.data;
    }

    async listFormTemplates() {
        const res = await axiosInstance.get('form-templates');
        const content = res.data || [];
        return {
            content,
            page: 0,
            totalPages: 1,
            totalElements: content.length,
            last: true
        };
    }

    async upsertFormTemplate(data) {
        const res = await axiosInstance.post('form-templates', data);
        return res.data;
    }

    // Build a resume and copy it into another user's account.
    async assignPlan(payload) {
        const res = await axiosInstance.post("users/plan", payload);
        return res.data;
    }

    async assignResume(payload) {
        const res = await axiosInstance.post('users/resume', payload);
        return res.data;
    }

    async sendBulkMail(payload) {
        const res = await axiosInstance.post('send-mail', payload);
        return res.data;
    }
}

export default new InternalService();
