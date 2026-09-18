import { useState, useEffect, useRef } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import {
    DocumentTextIcon,
    EllipsisHorizontalIcon,
    CodeBracketIcon,
    PencilSquareIcon,
    MagnifyingGlassIcon,
    ChevronLeftIcon,
    ChevronRightIcon,
} from '@heroicons/react/24/outline';
import docService from '../../services/doc.service';
import authService from '../../services/auth.service';
import { TEMPLATE_MAP } from '../../resume-template/registry';

const hasFormBuilder = (code) => !!code && !!TEMPLATE_MAP[code];

const CATEGORIES = [
    { key: 'CV_AND_RESUME', label: 'CV & Resume' },
    { key: 'COVER_LETTER', label: 'Cover Letter' },
    { key: 'JOURNAL_ARTICLES', label: 'Journal Articles' },
    { key: 'BOOKS', label: 'Books' },
    { key: 'CALENDARS', label: 'Calendars' },
    { key: 'FORMAL_LETTERS', label: 'Formal Letters' },
    { key: 'ASSIGNMENTS', label: 'Assignments' },
    { key: 'NEWSLETTERS', label: 'Newsletters' },
    { key: 'PRESENTATIONS', label: 'Presentations' },
    { key: 'REPORTS', label: 'Reports' },
];

const PAGE_SIZE_OPTIONS = [50, 80, 100];
const DEFAULT_PAGE_SIZE = 50;

const STATUS_BADGE = {
    READY: 'bg-emerald-50 text-emerald-600',
    PENDING: 'bg-amber-50 text-amber-600',
    COMPILING: 'bg-amber-50 text-amber-600',
    FAILED: 'bg-red-50 text-red-600',
};

// ---------------------------------------------------------------------------
// TemplateCard
// ---------------------------------------------------------------------------
function TemplateCard({ doc, onAction, isBusy, variant, isUserDocs }) {
    const [menuOpen, setMenuOpen] = useState(false);
    const menuRef = useRef(null);

    useEffect(() => {
        const h = (e) => { if (menuRef.current && !menuRef.current.contains(e.target)) setMenuOpen(false); };
        document.addEventListener('mousedown', h);
        return () => document.removeEventListener('mousedown', h);
    }, []);

    const choose = (m) => { setMenuOpen(false); onAction(doc, m); };
    const formAvailable = hasFormBuilder(doc.templateCode);

    const renderBadge = () => {
        if (variant === 'latex') return null;

        const isUnlocked = isUserDocs && doc.unlocked;
        const isUnlockedTemplate = !isUserDocs && doc.unlocked; // template the user has previously unlocked

        if (isUnlocked || isUnlockedTemplate) {
            return (
                <span className="absolute right-2 top-2 inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-[10px] font-bold uppercase tracking-wide text-white shadow-sm ring-1 ring-black/5 backdrop-blur-sm bg-emerald-500/95">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.4" className="h-3 w-3">
                        <path strokeLinecap="round" strokeLinejoin="round" d="M8 11V7a4 4 0 018 0v4M6 11h12v10H6z" />
                    </svg>
                    Unlocked
                </span>
            );
        }

        if (!doc.subscriptionType) return null;

        const isFree = doc.subscriptionType === 'FREE';
        return (
            <span className={`absolute right-2 top-2 inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-[10px] font-bold uppercase tracking-wide text-white shadow-sm ring-1 ring-black/5 backdrop-blur-sm ${isFree ? 'bg-emerald-500/95' : 'bg-amber-500/95'}`}>
                {isFree ? (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" className="h-3 w-3"><path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" /></svg>
                ) : (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.4" className="h-3 w-3"><path strokeLinecap="round" strokeLinejoin="round" d="M16.5 10.5V7a4.5 4.5 0 10-9 0v3.5M6 10.5h12v9H6z" /></svg>
                )}
                {isFree ? 'Free' : 'Paid'}
            </span>
        );
    };

    // Default click action based on variant
    const defaultAction = variant === 'form' ? 'form' : 'latex';

    return (
        <div
            onClick={() => !isBusy && choose(defaultAction)}
            className="group relative flex cursor-pointer flex-col overflow-hidden rounded-md border border-border bg-card shadow-sm transition-all duration-300 hover:-translate-y-1 hover:border-accent hover:shadow-lg hover:ring-1 hover:ring-accent"
        >
            <div className="relative flex items-center justify-center overflow-hidden bg-muted" style={{ aspectRatio: '3/4' }}>
                {doc.imageUrl ? (
                    <img
                        src={doc.imageUrl}
                        alt={doc.name}
                        className="h-full w-full object-cover object-top transition-transform duration-300 group-hover:scale-[1.03]"
                        onError={(e) => { e.currentTarget.style.display = 'none'; }}
                    />
                ) : (
                    <DocumentTextIcon className="h-12 w-12 text-muted-foreground" />
                )}
                {doc.status && doc.status !== 'READY' && (
                    <span className={`absolute left-2 top-2 rounded-full px-2 py-0.5 text-[10px] font-semibold ${STATUS_BADGE[doc.status] || 'bg-muted text-muted-foreground'}`}>
                        {doc.status}
                    </span>
                )}
                {renderBadge()}
                {isBusy && (
                    <div className="absolute inset-0 flex items-center justify-center bg-black/40 text-xs font-semibold text-white">
                        Opening…
                    </div>
                )}
            </div>

            <div className="flex items-center px-3 py-2.5">
                <p className="truncate text-sm font-medium text-foreground" title={doc.name}>{doc.name}</p>
            </div>
        </div>
    );
}

// ---------------------------------------------------------------------------
// SkeletonCard
// ---------------------------------------------------------------------------
function SkeletonCard() {
    return (
        <div className="flex flex-col overflow-hidden rounded-md border border-border bg-card animate-pulse">
            <div className="bg-muted" style={{ aspectRatio: '3/4' }} />
            <div className="flex items-center justify-between px-3 py-2.5">
                <div className="h-3 w-28 rounded bg-muted" />
                <div className="h-7 w-7 rounded-full bg-muted" />
            </div>
        </div>
    );
}

// ---------------------------------------------------------------------------
// Pagination
// ---------------------------------------------------------------------------
function Pagination({ page, totalPages, pageSize, onPageChange, onPageSizeChange }) {
    const safeTotal = Math.max(totalPages, 1);
    const buildPages = () => {
        const pages = [];
        const range = [];
        for (let i = Math.max(2, page - 1); i <= Math.min(safeTotal - 1, page + 1); i++) range.push(i);
        if (page - 1 > 2) range.unshift('...');
        if (page + 1 < safeTotal - 1) range.push('...');
        if (safeTotal > 1) {
            pages.push(1);
            range.forEach((r) => pages.push(r));
            pages.push(safeTotal);
        } else {
            pages.push(1);
        }
        return pages;
    };

    return (
        <div className="mt-10 flex flex-col items-center gap-4 sm:flex-row sm:justify-between">
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <span>Rows per page:</span>
                <select value={pageSize} onChange={(e) => onPageSizeChange(Number(e.target.value))}
                    className="rounded-lg border border-border bg-card px-2 py-1 text-sm text-muted-foreground focus:border-accent focus:outline-none">
                    {PAGE_SIZE_OPTIONS.map((s) => <option key={s} value={s}>{s}</option>)}
                </select>
            </div>

            <div className="flex items-center gap-1.5">
                <button onClick={() => onPageChange(page - 1)} disabled={page === 1}
                    className="flex h-8 w-8 items-center justify-center rounded-lg border border-border text-muted-foreground transition hover:border-accent hover:text-accent disabled:cursor-not-allowed disabled:opacity-40">
                    <ChevronLeftIcon className="h-4 w-4" />
                </button>
                {buildPages().map((p, idx) =>
                    p === '...' ? (
                        <span key={`e-${idx}`} className="flex h-8 w-8 items-center justify-center text-sm text-muted-foreground">…</span>
                    ) : (
                        <button key={p} onClick={() => onPageChange(p)}
                            className={`flex h-8 w-8 items-center justify-center rounded-lg border text-sm font-medium transition ${p === page ? 'border-accent bg-accent/10 text-accent' : 'border-border text-muted-foreground hover:border-accent hover:text-accent'}`}>
                            {p}
                        </button>
                    )
                )}
                <button onClick={() => onPageChange(page + 1)} disabled={page >= safeTotal}
                    className="flex h-8 w-8 items-center justify-center rounded-lg border border-border text-muted-foreground transition hover:border-accent hover:text-accent disabled:cursor-not-allowed disabled:opacity-40">
                    <ChevronRightIcon className="h-4 w-4" />
                </button>
            </div>

            <p className="text-sm text-muted-foreground">
                Page <span className="font-medium text-foreground">{page}</span> of <span className="font-medium text-foreground">{safeTotal}</span>
            </p>
        </div>
    );
}

// ---------------------------------------------------------------------------
// Templates (main export)
// ---------------------------------------------------------------------------
// variant: 'form'  — form-based, premium templates only (hasFormBuilder === true)
// variant: 'latex' — all templates, all free, rate-limited to 20 compiles/day server-side
// mode: 'user-docs' — My Templates page (user's saved docs)
export default function Templates({ mode = 'templates', variant = 'form' }) {
    const isUserDocs = mode === 'user-docs';
    const isLatex = variant === 'latex';

    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();

    const activeCategory = searchParams.get('type') || CATEGORIES[0].key;
    const page = Number(searchParams.get('page')) || 1;
    const pageSize = Number(searchParams.get('size')) || DEFAULT_PAGE_SIZE;
    const keyword = searchParams.get('keyword') || '';

    const [inputValue, setInputValue] = useState(keyword);
    const debounceRef = useRef(null);

    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [busyId, setBusyId] = useState(null);

    const handleAction = async (doc, action = 'form') => {
        if (busyId) return;

        if (action === 'form') {
            navigate(`/resume-builder/${doc.templateCode || 'classic'}`);
            return;
        }

        // latex action
        setBusyId(doc.id);
        try {
            if (isUserDocs) {
                const full = await docService.getUserDoc(doc.id);
                navigate(`/doc-editor/${doc.id}`, { state: { doc: full, fromVariant: variant } });
            } else {
                if (!authService.isAuthenticated()) {
                    toast('Sign in to edit LaTeX');
                    navigate('/login', { state: { from: window.location.pathname + window.location.search } });
                    return;
                }
                const saved = await docService.saveTemplateToAccount(doc.id);
                navigate(`/doc-editor/${saved.id}`, { state: { doc: saved, fromVariant: variant } });
            }
        } catch (err) {
            toast.error(err?.response?.data?.message || 'Failed to open the editor');
            setBusyId(null);
        }
    };

    useEffect(() => {
        if (!searchParams.has('type')) {
            setSearchParams({ type: CATEGORIES[0].key, page: 1, size: DEFAULT_PAGE_SIZE }, { replace: true });
        }
    }, []);

    useEffect(() => { setInputValue(keyword); }, [keyword]);

    useEffect(() => () => clearTimeout(debounceRef.current), []);

    useEffect(() => {
        setLoading(true);
        setError(null);
        setData(null);

        if (isUserDocs) {
            Promise.all([
                docService.listUserDocs({ type: activeCategory, keyword, page: page - 1, size: pageSize }),
                import('../../services/resume-builder.service').then(m => 
                    m.default.listDocuments({ type: activeCategory, keyword, page: page - 1, size: pageSize })
                )
            ]).then(([latexRes, formRes]) => {
                const formDocsMarked = (formRes || []).map(d => ({ ...d, _isForm: true }));
                const latexDocsMarked = (latexRes?.content || []).map(d => ({ ...d, _isLatex: true }));
                const combinedContent = [...formDocsMarked, ...latexDocsMarked];
                combinedContent.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
                setData({
                    content: combinedContent,
                    totalElements: (latexRes?.totalElements || 0) + (formRes?.length || 0),
                    totalPages: 1
                });
            })
            .catch((err) => setError(err?.response?.data?.message || 'Failed to load templates'))
            .finally(() => setLoading(false));
        } else {
            const fetcher = isLatex 
                ? docService.listLatexTemplates.bind(docService)
                : docService.listFormTemplates.bind(docService);
                
            fetcher({ type: activeCategory, keyword, page: page - 1, size: pageSize })
                .then((result) => setData(result))
                .catch((err) => setError(err?.response?.data?.message || 'Failed to load templates'))
                .finally(() => setLoading(false));
        }
    }, [isUserDocs, isLatex, activeCategory, page, pageSize, keyword]);

    const buildParams = (overrides) => {
        const base = { type: activeCategory, page, size: pageSize };
        if (keyword) base.keyword = keyword;
        return { ...base, ...overrides };
    };

    const handleSearchChange = (e) => {
        const val = e.target.value;
        setInputValue(val);
        clearTimeout(debounceRef.current);
        debounceRef.current = setTimeout(() => {
            const next = { type: activeCategory, page: 1, size: pageSize };
            if (val.trim()) next.keyword = val.trim();
            setSearchParams(next);
        }, 400);
    };

    const handleCategoryChange = (key) => {
        setInputValue('');
        setSearchParams({ type: key, page: 1, size: pageSize });
    };

    const templates = data?.content ?? [];
    const totalPages = data?.totalPages ?? 1;
    const totalElements = data?.totalElements ?? 0;

    // My Templates — split by doc type based on explicit tags
    const formDocs  = isUserDocs ? templates.filter((d) => d._isForm) : [];
    const latexDocs = isUserDocs ? templates.filter((d) => d._isLatex) : [];
    const formUnlocked  = formDocs.filter((d) => d.unlocked);
    const formLocked    = formDocs.filter((d) => !d.unlocked);
    const latexUnlocked = latexDocs.filter((d) => d.unlocked);
    const latexLocked   = latexDocs.filter((d) => !d.unlocked);

    // docGrid accepts an optional variantOverride so each section renders the right card behaviour
    const docGrid = (items, variantOverride = variant) => (
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {items.map((doc) => (
                <TemplateCard
                    key={doc.id}
                    doc={doc}
                    isBusy={busyId === doc.id}
                    onAction={handleAction}
                    variant={variantOverride}
                    isUserDocs={isUserDocs}
                />
            ))}
        </div>
    );

    const sectionHeading = (label) => (
        <h3 className="mb-3 text-sm font-bold uppercase tracking-wide text-muted-foreground">{label}</h3>
    );

    const lockedNote = <span className="font-normal normal-case text-muted-foreground"> — locked, unlock to download</span>;

    return (
        <div className="min-h-screen">
            <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8 border-l border-r border-border">

                {/* Category filter tabs */}
                <div className="mb-8 -mx-4 overflow-x-auto px-4 sm:mx-0 sm:px-0">
                    <div className="flex gap-2 pb-1 sm:flex-wrap">
                        {CATEGORIES.map((cat) => (
                            <button key={cat.key} onClick={() => handleCategoryChange(cat.key)}
                                className={`whitespace-nowrap rounded-full px-4 py-1.5 text-sm font-medium transition-all duration-200 ${activeCategory === cat.key ? 'bg-accent text-accent-foreground shadow-sm' : 'border border-border bg-card text-muted-foreground hover:border-accent hover:text-accent'}`}>
                                {cat.label}
                            </button>
                        ))}
                    </div>
                </div>

                {/* Search */}
                <div className="mb-8">
                    <div className="mx-auto w-full max-w-md">
                        <div className="relative">
                            <MagnifyingGlassIcon className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                            <input type="text" value={inputValue} onChange={handleSearchChange}
                                placeholder={isUserDocs ? 'Search my documents…' : 'Search templates…'}
                                className="w-full rounded-full border border-border bg-card py-2.5 pl-9 pr-4 text-sm text-foreground placeholder-muted-foreground shadow-sm outline-none transition focus:border-accent focus:ring-2 focus:ring-accent/40" />
                        </div>
                    </div>
                </div>

                {/* Result count */}
                {data && !loading && (
                    <p className="mb-5 text-sm text-muted-foreground">
                        {totalElements} {isUserDocs ? 'document' : 'template'}{totalElements !== 1 ? 's' : ''} in{' '}
                        <span className="font-medium text-foreground">{CATEGORIES.find((c) => c.key === activeCategory)?.label}</span>
                    </p>
                )}

                {/* Error state */}
                {error && (
                    <div className="flex flex-col items-center gap-3 py-24 text-center">
                        <p className="text-sm text-muted-foreground">{error}</p>
                        <button onClick={() => setSearchParams(buildParams({ page: 1 }))}
                            className="mt-1 rounded-full bg-accent px-4 py-2 text-sm font-medium text-accent-foreground hover:bg-accent-hover">Retry</button>
                    </div>
                )}

                {/* Empty state */}
                {!loading && !error && templates.length === 0 && (
                    <div className="py-24 text-center text-muted-foreground">
                        {isUserDocs
                            ? 'You have not saved any documents yet.'
                            : isLatex
                            ? 'No LaTeX templates available in this category yet.'
                            : 'No form-based templates available in this category yet.'}
                    </div>
                )}

                {/* Loading skeleton */}
                {!error && loading && (
                    <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                        {Array.from({ length: pageSize }).map((_, i) => <SkeletonCard key={i} />)}
                    </div>
                )}

                {/* User docs (My Templates) — two groups: form-based and LaTeX */}
                {!error && !loading && isUserDocs && (
                    <>
                        {/* ── Form-based ── */}
                        {formDocs.length > 0 && (
                            <section className="mb-10">
                                <div className="mb-4 flex items-center gap-3">
                                    <h2 className="text-base font-semibold text-foreground">Form-based</h2>
                                    <span className="rounded-full bg-accent/10 px-2.5 py-0.5 text-xs font-medium text-accent">{formDocs.length}</span>
                                </div>
                                {formUnlocked.length > 0 && (
                                    <section className="mb-6">
                                        {sectionHeading('Your resumes')}
                                        {docGrid(formUnlocked, 'form')}
                                    </section>
                                )}
                                {formLocked.length > 0 && (
                                    <section>
                                        <h3 className="mb-3 text-sm font-bold uppercase tracking-wide text-muted-foreground">
                                            Recently used{lockedNote}
                                        </h3>
                                        {docGrid(formLocked, 'form')}
                                    </section>
                                )}
                            </section>
                        )}

                        {/* ── LaTeX ── */}
                        {latexDocs.length > 0 && (
                            <section>
                                <div className="mb-4 flex items-center gap-3">
                                    <h2 className="text-base font-semibold text-foreground">LaTeX</h2>
                                    <span className="rounded-full bg-accent/10 px-2.5 py-0.5 text-xs font-medium text-accent">{latexDocs.length}</span>
                                </div>
                                {latexUnlocked.length > 0 && (
                                    <section className="mb-6">
                                        {sectionHeading('Your documents')}
                                        {docGrid(latexUnlocked, 'latex')}
                                    </section>
                                )}
                                {latexLocked.length > 0 && (
                                    <section>
                                        <h3 className="mb-3 text-sm font-bold uppercase tracking-wide text-muted-foreground">
                                            Recently used{lockedNote}
                                        </h3>
                                        {docGrid(latexLocked, 'latex')}
                                    </section>
                                )}
                            </section>
                        )}
                    </>
                )}

                {/* Template grid (explore pages) */}
                {!error && !loading && !isUserDocs && docGrid(templates)}

                {/* Pagination */}
                {!loading && !error && totalElements > 0 && (
                    <Pagination page={page} totalPages={totalPages} pageSize={pageSize}
                        onPageChange={(p) => setSearchParams(buildParams({ page: p }))}
                        onPageSizeChange={(s) => setSearchParams(buildParams({ page: 1, size: s }))} />
                )}
            </div>
        </div>
    );
}
