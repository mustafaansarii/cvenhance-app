import { useEffect, useRef, useState } from 'react';
import toast from 'react-hot-toast';
import userService from '../../services/user.service';
import { textFromFile, MAX_UPLOAD_BYTES, ResumeUploadError } from '../../utils/resumeText';

export default function MagicWriteBot({ open, onOpenChange, onDone }) {
    const inputRef = useRef(null);
    const [busy, setBusy] = useState(false);
    const [reading, setReading] = useState(false);
    const [fileName, setFileName] = useState('');
    const [fileText, setFileText] = useState('');
    const [jd, setJd] = useState('');

    const toggleOpen = () => {
        if (!open) {
            setFileName('');
            setFileText('');
            setJd('');
        }
        onOpenChange?.(!open);
    };

    const onChange = async (e) => {
        const file = e.target.files?.[0];
        e.target.value = '';
        if (!file) return;
        if (file.size > MAX_UPLOAD_BYTES) {
            toast.error('File is too large. Please upload a file under 5 MB.');
            return;
        }
        setReading(true);
        try {
            const text = await textFromFile(file);
            if (!text || !text.trim()) {
                toast.error("Couldn't read any text from this file.");
                return;
            }
            setFileName(file.name);
            setFileText(text);
            toast.success('File attached!');
        } catch (err) {
            if (err instanceof ResumeUploadError) toast.error(err.message);
            else toast.error('Failed to read file. Please try another.');
        } finally {
            setReading(false);
        }
    };

    const submit = async () => {
        setBusy(true);
        const tailoring = !!jd.trim();
        const id = toast.loading(tailoring ? 'Tailoring your resume…' : 'Importing data…');
        try {
            const profile = await userService.importResume(fileText, jd.trim());
            toast.success('Resume updated successfully!', { id });
            onDone?.(profile);
            onOpenChange?.(false);
        } catch (err) {
            toast.error(err?.response?.data?.message || 'Failed to process instructions', { id });
        } finally {
            setBusy(false);
        }
    };

    const canSubmit = !!fileText || !!jd.trim();

    return (
        <div className="no-print fixed bottom-4 right-4 z-[100000] flex flex-col items-end sm:bottom-6 sm:right-6">
            <input ref={inputRef} type="file" accept=".pdf,.docx,.txt" className="hidden" onChange={onChange} />
            
            {open && (
                <div className="mb-4 w-[calc(100vw-2rem)] max-w-sm overflow-hidden rounded-2xl border border-border bg-card shadow-2xl transition-all sm:w-[380px]">
                    <div className="flex items-center justify-between border-b border-border bg-accent/5 px-4 py-3">
                        <div className="flex items-center gap-2 text-accent">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="h-5 w-5"><path strokeLinecap="round" strokeLinejoin="round" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" /></svg>
                            <h3 className="font-semibold text-foreground">Magic Write Assistant</h3>
                        </div>
                        <button onClick={toggleOpen} className="text-muted-foreground hover:text-foreground">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" className="h-5 w-5"><path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
                        </button>
                    </div>
                    
                    <div className="p-4">
                        <p className="mb-4 text-sm text-muted-foreground">
                            Upload your resume to auto-fill the template, or paste a job description / instructions to tailor the content.
                        </p>
                        
                        <div className="mb-3">
                            <textarea
                                value={jd}
                                onChange={(e) => setJd(e.target.value)}
                                rows={4}
                                placeholder="E.g., Make my resume fit this job description..."
                                className="w-full resize-none rounded-xl border border-border bg-background p-3 text-sm text-foreground outline-none transition focus:border-accent focus:ring-1 focus:ring-accent"
                            />
                        </div>
                        
                        <div className="flex items-center gap-2">
                            <button
                                onClick={() => inputRef.current?.click()}
                                disabled={reading || busy}
                                className="flex shrink-0 items-center gap-2 rounded-xl border border-border bg-muted/50 px-3 py-2 text-sm font-semibold text-foreground transition hover:bg-muted disabled:opacity-60"
                            >
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="h-4 w-4"><path strokeLinecap="round" strokeLinejoin="round" d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" /></svg>
                                {reading ? 'Reading...' : (fileName ? 'Change File' : 'Attach File')}
                            </button>
                            <span className="min-w-0 flex-1 truncate text-xs text-muted-foreground">
                                {fileName || 'No file attached'}
                            </span>
                            {fileName && !busy && (
                                <button onClick={() => { setFileName(''); setFileText(''); }} className="shrink-0 p-1 text-red-500 hover:text-red-600">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" className="h-4 w-4"><path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" /></svg>
                                </button>
                            )}
                        </div>
                    </div>
                    
                    <div className="border-t border-border bg-muted/30 p-3">
                        <button
                            onClick={submit}
                            disabled={!canSubmit || busy}
                            className="w-full rounded-xl bg-accent py-2.5 text-sm font-bold text-accent-foreground shadow transition hover:bg-accent-hover disabled:opacity-50"
                        >
                            {busy ? 'Working...' : 'Magic Write ✨'}
                        </button>
                    </div>
                </div>
            )}
            
            <button
                onClick={toggleOpen}
                className="flex h-14 w-14 items-center justify-center rounded-full bg-accent text-accent-foreground shadow-2xl transition hover:scale-105 hover:bg-accent-hover focus:outline-none focus:ring-4 focus:ring-accent/30"
                title="Magic Write"
            >
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="h-6 w-6"><path strokeLinecap="round" strokeLinejoin="round" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143z" /></svg>
            </button>
        </div>
    );
}
