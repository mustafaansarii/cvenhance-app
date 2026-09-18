import { useEffect, useState } from 'react';
import { XMarkIcon, SparklesIcon, PaperAirplaneIcon } from '@heroicons/react/24/outline';
import ResumeUploadButton from '../profile/ResumeUploadButton';

export default function AiChatPanel({ open, onClose, onUploadDone }) {
    const [input, setInput] = useState('');
    const [messages, setMessages] = useState([
        { role: 'assistant', content: "Hi! I'm your AI Resume Assistant. You can upload an existing resume to start, or type instructions below to build or modify your resume." }
    ]);

    useEffect(() => {
        if (!open) return;
        // Optionally reset state
    }, [open]);

    if (!open) return null;

    const handleSend = (e) => {
        e.preventDefault();
        if (!input.trim()) return;
        setMessages([...messages, { role: 'user', content: input }]);
        setInput('');
        
        // Mock a response for now
        setTimeout(() => {
            setMessages((prev) => [...prev, { role: 'assistant', content: "I'm still learning how to chat! For now, you can use the upload button above to fill your resume automatically." }]);
        }, 1000);
    };

    return (
        <div className="fixed inset-y-0 right-0 z-[100] flex w-full max-w-sm flex-col border-l border-border bg-card shadow-2xl transition-transform">
            <div className="flex shrink-0 items-center justify-between border-b border-border bg-muted/30 px-4 py-3">
                <div className="flex items-center gap-2 font-bold text-foreground">
                    <SparklesIcon className="h-5 w-5 text-teal-600" />
                    AI Magic Write
                </div>
                <button onClick={onClose} className="rounded p-1 text-muted-foreground hover:bg-muted hover:text-foreground">
                    <XMarkIcon className="h-5 w-5" />
                </button>
            </div>

            <div className="flex-1 overflow-y-auto p-4 flex flex-col gap-4">
                {messages.map((m, i) => (
                    <div key={i} className={`flex ${m.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                        <div className={`max-w-[85%] rounded-2xl px-4 py-2 text-[0.9em] ${m.role === 'user' ? 'bg-teal-600 text-white' : 'bg-muted text-foreground'}`}>
                            {m.content}
                        </div>
                    </div>
                ))}

                <div className="mt-2 flex justify-start">
                    <ResumeUploadButton
                        label="Upload Resume"
                        icon={<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className="h-4 w-4 shrink-0"><path strokeLinecap="round" strokeLinejoin="round" d="M12 20h9M16.5 3.5a2.12 2.12 0 013 3L7 19l-4 1 1-4 12.5-12.5z" /></svg>}
                        confirm="We'll read your uploaded file and automatically fill in your resume for you. This replaces the details currently on screen."
                        className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-teal-50 px-4 py-3 text-sm font-semibold text-teal-700 transition hover:bg-teal-100"
                        onDone={(p) => {
                            setMessages((prev) => [...prev, { role: 'assistant', content: 'Resume uploaded successfully! I have applied it to the editor.' }]);
                            if (onUploadDone) onUploadDone(p);
                        }}
                    />
                </div>
            </div>

            <form onSubmit={handleSend} className="shrink-0 border-t border-border bg-card p-3">
                <div className="relative flex items-center">
                    <input
                        type="text"
                        placeholder="Ask me to rewrite or add section..."
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        className="w-full rounded-full border border-border bg-muted/50 py-2.5 pl-4 pr-12 text-sm text-foreground outline-none focus:border-teal-500 focus:bg-transparent"
                    />
                    <button
                        type="submit"
                        disabled={!input.trim()}
                        className="absolute right-1.5 flex h-8 w-8 items-center justify-center rounded-full bg-teal-600 text-white transition hover:bg-teal-700 disabled:opacity-50"
                    >
                        <PaperAirplaneIcon className="h-4 w-4" />
                    </button>
                </div>
            </form>
        </div>
    );
}
