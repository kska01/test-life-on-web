import api from './axios';

const ENDPOINT = '/plan-files';
const fileUploadApi = {
  
  uploadFile: async (formData) => {
    const response = await api.post(`${ENDPOINT}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  },
};

export default fileUploadApi;
