import { useState } from 'react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';

const MotionDiv = motion.div;

const fadeUp = {
    hidden: { opacity: 0, y: 24 },
    show: (i = 0) => ({ opacity: 1, y: 0, transition: { duration: 0.5, delay: i * 0.08 } }),
};
const viewport = { once: true, amount: 0.2 };

const TRUST_LOGOS = ['Google', 'Amazon', 'Microsoft', 'Meta', 'Netflix', 'Stripe'];

const STATS = [
    { value: '500K+', label: 'documents created', tint: 'bg-muted text-foreground' },
    { value: '40+', label: 'professional templates', tint: 'bg-muted text-foreground' },
    { value: '6 years', label: 'helping job seekers', tint: 'bg-muted text-foreground' },
    { value: '98%', label: 'pass ATS checks', tint: 'bg-muted text-foreground' },
];

const FEATURES = [
    { title: 'AI-assisted writing', desc: 'Turn rough notes into polished, recruiter-ready bullet points in seconds.', path: 'M9.5 3a1 1 0 011 1 4 4 0 004 4 1 1 0 010 2 4 4 0 00-4 4 1 1 0 01-2 0 4 4 0 00-4-4 1 1 0 010-2 4 4 0 004-4 1 1 0 011-1z' },
    { title: 'ATS-friendly', desc: 'Clean, parsable layouts that sail through applicant tracking systems.', path: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z' },
    { title: 'Live PDF preview', desc: 'Edit on the left, watch your compiled PDF update live on the right.', path: 'M15 12a3 3 0 11-6 0 3 3 0 016 0zM2.5 12C3.7 7.9 7.5 5 12 5s8.3 2.9 9.5 7c-1.2 4.1-5 7-9.5 7s-8.3-2.9-9.5-7z' },
    { title: 'Designer templates', desc: 'Dozens of professionally crafted templates for every document type.', path: 'M4 5a1 1 0 011-1h14a1 1 0 011 1v4H4V5zm0 6h7v9H5a1 1 0 01-1-1v-8zm9 0h7v8a1 1 0 01-1 1h-6v-9z' },
];

const TEMPLATES = [
    { src: 'https://cdn.enhancv.com/images/1098/i/aHR0cHM6Ly9jZG4uZW5oYW5jdi5jb20vcHJlZGVmaW5lZC1leGFtcGxlcy9vU0ZjUElJdk1rVUhzT2xQQ0gwU3NLRUF0aVprd0N6Q2xPTFRFUFJmL2ltYWdlLnBuZw~~.png', name: 'Enhancv Style', tag: 'Resume', to: '/templates?type=CV_AND_RESUME' },
    { src: 'https://i.ibb.co/7HgSdbL/William-Lucas.jpg', name: 'Engineering Pro', tag: 'Resume', to: '/templates?type=CV_AND_RESUME' },
    { src: 'https://i.ibb.co/v4dWrK1j/Vishnu-Singh.jpg', name: 'Modern Tech', tag: 'Resume', to: '/templates?type=CV_AND_RESUME' },
    { src: 'http://raw.githubusercontent.com/jakegut/resume/refs/heads/master/resume.png', name: 'Classic Professional', tag: 'Resume', to: '/templates?type=CV_AND_RESUME' },
];

const WORKFLOW_STEPS = [
    { title: 'Provide your details', desc: 'Upload your existing resume or type in your experience from scratch.', icon: 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z' },
    { title: 'AI-assisted writing', desc: 'Let our AI rewrite and enhance your bullet points for maximum impact.', icon: 'M13 10V3L4 14h7v7l9-11h-7z' },
    { title: 'Analyze & Score', desc: 'Get an instant ATS compatibility score and actionable feedback.', icon: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z' },
    { title: 'One-click fix', desc: 'Automatically resolve formatting and keyword issues with a single click.', icon: 'M15 15l-2 5L9 9l11 4-5 2zm0 0l5 5M7.188 2.239l.777 2.897M5.136 7.965l-2.898-.777M13.95 4.05l-2.122 2.122m-5.657 5.656l-2.12 2.122' },
    { title: 'Apply & get hired', desc: 'Download your polished, recruiter-ready PDF and start landing interviews.', icon: 'M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z' },
];

const V_WIRE_H = 2400;
const V_WIRE_W = 80;
const V_WAVE = 200;
const V_BASE = V_WIRE_W / 2;
const V_WIRES = [
    { color: '#c96442', amp: 12, phase: 0.0, dur: 3.2, opacity: 0.5 },
    { color: '#c96442', amp: 16, phase: 2.2, dur: 2.6, opacity: 0.3 },
    { color: '#c96442', amp: 8, phase: 4.2, dur: 3.9, opacity: 0.7 },
];

function wavePathVertical(amp, phase) {
    let d = `M ${V_BASE} 0`;
    for (let y = 0; y <= V_WIRE_H; y += 10) {
        const x = V_BASE + amp * Math.sin((y / V_WAVE) * Math.PI * 2 + phase);
        d += ` L ${x.toFixed(1)} ${y}`;
    }
    return d;
}

function VerticalTwistedWire() {
    return (
        <svg viewBox={`0 0 ${V_WIRE_W} 1000`} preserveAspectRatio="none" className="h-full w-full">
            <style>{`@keyframes v-tw-slide { from { transform: translateY(0) } to { transform: translateY(-${V_WAVE * 2}px) } }`}</style>
            <defs>
                <linearGradient id="v-tw-fade" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0" stopColor="white" stopOpacity="0" />
                    <stop offset="0.05" stopColor="white" stopOpacity="1" />
                    <stop offset="0.95" stopColor="white" stopOpacity="1" />
                    <stop offset="1" stopColor="white" stopOpacity="0" />
                </linearGradient>
                <mask id="v-tw-mask">
                    <rect x="0" y="0" width="100%" height="100%" fill="url(#v-tw-fade)" />
                </mask>
            </defs>
            <g mask="url(#v-tw-mask)">
                {V_WIRES.map((w, i) => (
                    <g key={i} style={{ animation: `v-tw-slide ${w.dur}s linear infinite` }}>
                        <path d={wavePathVertical(w.amp, w.phase)} fill="none" stroke={w.color}
                            strokeWidth="2.5" strokeLinecap="round" opacity={w.opacity} />
                    </g>
                ))}
            </g>
        </svg>
    );
}

const REVIEWS = [
    { name: 'Khushboo S.', when: '22 hours ago', stars: 5, text: 'The best CV building tool — tailored output for every job description, and the look and feel is truly dynamic.' },
    { name: 'Aisha K.', when: '3 days ago', stars: 5, text: 'Rebuilt my resume in 20 minutes and started getting callbacks the same week.' },
    { name: 'Roseline', when: '4 days ago', stars: 5, text: 'Nicely surprised by the professional level of the templates.' },
    { name: 'Daniel R.', when: '5 days ago', stars: 4, text: 'Finally a builder that does not fight me on formatting. Genuinely professional.' },
];

const FAQS = [
    { q: 'Is CVEnhance free to use?', a: 'Yes. You can build, edit, and download a finished, recruiter-ready PDF resume completely for free.' },
    { q: 'Are the templates ATS-friendly?', a: 'Yes. All our templates use clean layouts and readable fonts that Applicant Tracking Systems can easily parse.' },
    { q: 'Do I need an account to build a resume?', a: 'An account is required to save your progress, so you can return anytime to edit, update, or download your documents.' },
    { q: 'Can I switch templates after filling in my details?', a: 'Yes. You can browse our collection and apply a different design to your resume at any time.' },
    { q: 'Can I add custom sections like Projects or Certifications?', a: 'Absolutely. You can easily add, rename, or reorder any section within the live editor.' }
];

function FaqItem({ item, isOpen, onToggle }) {
    return (
        <div className="rounded-2xl border border-border bg-card transition hover:border-accent">
            <button
                onClick={onToggle}
                className="flex w-full items-center justify-between gap-4 px-5 py-4 text-left"
                aria-expanded={isOpen}
            >
                <span className="text-sm font-semibold sm:text-base text-foreground">{item.q}</span>
                <svg
                    viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"
                    className={`h-5 w-5 shrink-0 text-accent transition-transform duration-300 ${isOpen ? 'rotate-180' : ''}`}
                >
                    <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                </svg>
            </button>
            <div className={`grid overflow-hidden px-5 transition-all duration-300 ease-in-out ${isOpen ? 'grid-rows-[1fr] pb-5 opacity-100' : 'grid-rows-[0fr] opacity-0'}`}>
                <div className="overflow-hidden">
                    <p className="text-sm leading-relaxed text-muted-foreground">{item.a}</p>
                </div>
            </div>
        </div>
    );
}

function FaqSection() {
    const [open, setOpen] = useState(0);
    return (
        <section className="border-t border-border">
            <div className="mx-auto max-w-3xl px-4 py-16 sm:px-6 lg:px-8">
                <MotionDiv variants={fadeUp} initial="hidden" whileInView="show" viewport={viewport} className="text-center">
                    <h2 className="font-serif text-3xl font-bold tracking-tight sm:text-4xl text-foreground">Frequently asked questions</h2>
                    <p className="mt-4 text-base text-muted-foreground">Everything you need to know about building documents with CVEnhance.</p>
                </MotionDiv>
                <div className="mt-12 space-y-3">
                    {FAQS.map((item, i) => (
                        <FaqItem key={item.q} item={item} isOpen={open === i} onToggle={() => setOpen(open === i ? -1 : i)} />
                    ))}
                </div>
            </div>
        </section>
    );
}

function FeatureIcon({ path }) {
    return (
        <div className="mb-4 inline-flex h-11 w-11 items-center justify-center rounded-xl bg-accent/10 text-accent">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className="h-6 w-6">
                <path strokeLinecap="round" strokeLinejoin="round" d={path} />
            </svg>
        </div>
    );
}

function Stars({ count = 5 }) {
    return (
        <div className="flex gap-0.5">
            {Array.from({ length: 5 }).map((_, i) => (
                <span key={i} className={`flex h-5 w-5 items-center justify-center rounded ${i < count ? 'bg-accent' : 'bg-muted'}`}>
                    <svg viewBox="0 0 20 20" fill="white" className="h-3 w-3">
                        <path d="M10 1.5l2.6 5.27 5.82.85-4.21 4.1.99 5.8L10 14.77l-5.2 2.75.99-5.8L1.58 7.62l5.82-.85L10 1.5z" />
                    </svg>
                </span>
            ))}
        </div>
    );
}

export default function HomeSections() {
    const templateCards = TEMPLATES;

    return (
        <div className="bg-background text-foreground">

            <section className="border-b border-border">
                <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
                    <p className="text-center text-xs font-semibold uppercase tracking-widest text-muted-foreground">
                        Trusted by job seekers hired at
                    </p>
                    <div className="mt-6 flex flex-wrap items-center justify-center gap-x-10 gap-y-4">
                        {TRUST_LOGOS.map((logo) => (
                            <span key={logo} className="text-lg font-bold tracking-tight text-muted-foreground">{logo}</span>
                        ))}
                    </div>
                </div>
            </section>


            <section className="border-b border-border bg-background">
                <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 sm:py-14 lg:px-8 lg:py-16">

                    {/* Section header */}
                    <div className="flex flex-col gap-5 sm:flex-row sm:items-end sm:justify-between">
                        <div className="max-w-2xl">
                            <div className="mb-3 flex items-center gap-2 text-xs font-semibold uppercase tracking-[0.15em] text-accent">
                                <span className="h-px w-5 bg-accent" />
                                Resume Templates
                            </div>

                            <h2 className="font-serif text-3xl font-bold tracking-tight text-foreground sm:text-4xl">
                                Create a resume that
                                <span className="text-accent"> gets noticed.</span>
                            </h2>

                            <p className="mt-3 max-w-xl text-sm leading-6 text-muted-foreground sm:text-base">
                                Professionally designed, ATS-friendly templates that help
                                you present your experience clearly and make a stronger
                                first impression.
                            </p>
                        </div>

                        <Link
                            to="/templates"
                            className="group inline-flex w-fit shrink-0 items-center gap-2 border border-border px-4 py-2.5 text-sm font-medium text-foreground transition-colors hover:border-accent hover:bg-accent hover:text-accent-foreground"
                        >
                            Browse all templates
                            <svg
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="2"
                                className="h-4 w-4 transition-transform duration-200 group-hover:translate-x-0.5"
                            >
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    d="M5 12h14M13 6l6 6-6 6"
                                />
                            </svg>
                        </Link>
                    </div>

                    {/* Templates */}
                    <div className="mt-9 grid grid-cols-1 gap-5 sm:mt-11 sm:grid-cols-2 sm:gap-5 lg:grid-cols-4 lg:gap-6">
                        {templateCards.map((t, i) => (
                            <MotionDiv
                                key={t.name + i}
                                custom={i}
                                variants={fadeUp}
                                initial="hidden"
                                whileInView="show"
                                viewport={viewport}
                                className={i > 0 ? 'hidden sm:block' : ''}
                            >
                                <Link
                                    to={t.to || '/templates'}
                                    className="group relative block overflow-hidden border border-border bg-card shadow-sm transition-all duration-300 hover:-translate-y-1 hover:border-accent/40 hover:shadow-lg"
                                    style={{ aspectRatio: '3 / 4' }}
                                >
                                    <img
                                        src={t.src}
                                        alt={`${t.name} resume template`}
                                        loading="lazy"
                                        className="h-full w-full object-cover object-top transition-transform duration-500 group-hover:scale-[1.02]"
                                        onError={(e) => {
                                            e.currentTarget.src =
                                                'https://placehold.co/600x800?text=Resume+Template';
                                        }}
                                    />

                                    {/* Hover overlay */}
                                    <div className="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/75 via-black/30 to-transparent px-4 pb-4 pt-12 opacity-0 transition-opacity duration-300 group-hover:opacity-100">
                                        <div className="flex items-center justify-between gap-3">
                                            <span className="text-sm font-semibold text-white">
                                                {t.name}
                                            </span>

                                            <span className="flex h-7 w-7 shrink-0 items-center justify-center border border-white/30 bg-white/10 text-white backdrop-blur-sm">
                                                <svg
                                                    viewBox="0 0 24 24"
                                                    fill="none"
                                                    stroke="currentColor"
                                                    strokeWidth="2"
                                                    className="h-3.5 w-3.5"
                                                >
                                                    <path
                                                        strokeLinecap="round"
                                                        strokeLinejoin="round"
                                                        d="M5 12h14M13 6l6 6-6 6"
                                                    />
                                                </svg>
                                            </span>
                                        </div>
                                    </div>
                                </Link>
                            </MotionDiv>
                        ))}
                    </div>



                    {/* Supporting points */}
                    <div className="mt-7 flex flex-wrap items-center justify-center gap-x-5 gap-y-2 text-xs text-muted-foreground">
                        <span>✓ ATS-friendly</span>
                        <span className="h-3 w-px bg-border" />
                        <span>✓ Fully editable</span>
                        <span className="h-3 w-px bg-border" />
                        <span>✓ Professional designs</span>
                    </div>
                </div>
            </section>




            <section className="relative overflow-hidden bg-background py-20 sm:py-24">
                {/* Ambient background */}
                <div className="pointer-events-none absolute inset-0">
                    <div className="absolute left-1/2 top-20 h-72 w-72 -translate-x-1/2 rounded-full bg-accent/5 blur-3xl" />
                    <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-border to-transparent" />
                </div>

                <div className="relative mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
                    {/* Header */}
                    <MotionDiv
                        variants={fadeUp}
                        initial="hidden"
                        whileInView="show"
                        viewport={viewport}
                        className="mx-auto max-w-2xl text-center"
                    >
                        <div className="inline-flex items-center gap-2 rounded-full border border-accent/20 bg-accent/5 px-3 py-1 text-[11px] font-bold uppercase tracking-[0.14em] text-accent">
                            <span className="h-1.5 w-1.5 rounded-full bg-accent shadow-[0_0_0_3px] shadow-accent/10" />
                            Simple workflow
                        </div>

                        <h2 className="mt-5 font-serif text-3xl font-bold tracking-tight text-foreground sm:text-4xl lg:text-5xl">
                            Your fastest path
                            <span className="block text-accent">to a new job</span>
                        </h2>

                        <p className="mx-auto mt-4 max-w-xl text-sm leading-6 text-muted-foreground sm:text-base">
                            Everything you need to go from searching to getting hired —
                            without the usual hassle.
                        </p>
                    </MotionDiv>

                    {/* Timeline */}
                    <div className="mx-auto mt-16 max-w-5xl">
                        <div className="relative">
                            {/* Desktop center line */}
                            <div className="pointer-events-none absolute bottom-8 left-1/2 top-8 hidden w-px -translate-x-1/2 md:block">
                                <div className="absolute inset-0 bg-gradient-to-b from-transparent via-accent/30 to-transparent" />
                                <div className="absolute inset-x-0 top-0 h-full overflow-hidden">
                                    <div className="h-full w-full bg-gradient-to-b from-accent/0 via-accent to-accent/0 opacity-30" />
                                </div>
                            </div>

                            {/* Mobile line */}
                            <div className="pointer-events-none absolute bottom-8 left-[23px] top-8 w-px bg-gradient-to-b from-transparent via-accent/25 to-transparent md:hidden" />

                            <div className="space-y-7 md:space-y-10">
                                {WORKFLOW_STEPS.map((step, i) => (
                                    <MotionDiv
                                        key={step.title}
                                        variants={fadeUp}
                                        initial="hidden"
                                        whileInView="show"
                                        viewport={viewport}
                                        transition={{ delay: i * 0.08 }}
                                        className="group relative"
                                    >
                                        {/* Step number */}
                                        <div className="absolute left-0 top-1/2 z-20 flex h-12 w-12 -translate-y-1/2 items-center justify-center md:left-1/2 md:-translate-x-1/2">
                                            <div className="absolute inset-0 rounded-full bg-background ring-1 ring-border" />
                                            <div className="relative flex h-9 w-9 items-center justify-center rounded-full bg-accent text-xs font-black text-white shadow-lg shadow-accent/20 transition-transform duration-300 group-hover:scale-110">
                                                {String(i + 1).padStart(2, "0")}
                                            </div>
                                        </div>

                                        {/* Content */}
                                        <div
                                            className={`pl-16 md:w-1/2 md:pl-0 ${i % 2 === 0
                                                ? "md:pr-16"
                                                : "md:ml-auto md:pl-16"
                                                }`}
                                        >
                                            <div className="relative overflow-hidden rounded-2xl border border-border/80 bg-card/90 p-5 shadow-[0_8px_30px_rgba(0,0,0,0.04)] backdrop-blur-sm transition-all duration-300 hover:-translate-y-1 hover:border-accent/30 hover:shadow-[0_16px_40px_rgba(0,0,0,0.08)]">
                                                {/* Hover accent */}
                                                <div className="absolute inset-y-0 left-0 w-1 origin-bottom scale-y-0 bg-accent transition-transform duration-300 group-hover:scale-y-100" />

                                                <div className="flex items-start gap-4">
                                                    {/* Icon */}
                                                    <div className="relative flex h-12 w-12 shrink-0 items-center justify-center rounded-xl border border-accent/10 bg-accent/10 text-accent transition-all duration-300 group-hover:bg-accent group-hover:text-white">
                                                        <svg
                                                            viewBox="0 0 24 24"
                                                            fill="none"
                                                            stroke="currentColor"
                                                            strokeWidth="1.8"
                                                            className="h-6 w-6"
                                                        >
                                                            <path
                                                                strokeLinecap="round"
                                                                strokeLinejoin="round"
                                                                d={step.icon}
                                                            />
                                                        </svg>
                                                    </div>

                                                    {/* Text */}
                                                    <div className="min-w-0 pt-0.5">
                                                        <div className="mb-1 text-[10px] font-bold uppercase tracking-[0.15em] text-accent/70">
                                                            Step {i + 1}
                                                        </div>

                                                        <h3 className="text-base font-bold tracking-tight text-foreground sm:text-lg">
                                                            {step.title}
                                                        </h3>

                                                        <p className="mt-1.5 text-sm leading-5 text-muted-foreground">
                                                            {step.desc}
                                                        </p>
                                                    </div>

                                                    {/* Arrow */}
                                                    <div className="ml-auto hidden shrink-0 text-muted-foreground/30 transition-all duration-300 group-hover:translate-x-1 group-hover:text-accent sm:block">
                                                        <svg
                                                            viewBox="0 0 20 20"
                                                            fill="none"
                                                            className="h-5 w-5"
                                                        >
                                                            <path
                                                                d="M4 10h11M11 5l5 5-5 5"
                                                                stroke="currentColor"
                                                                strokeWidth="1.5"
                                                                strokeLinecap="round"
                                                                strokeLinejoin="round"
                                                            />
                                                        </svg>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </MotionDiv>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
            </section>


            <section className="border-b border-border border-t">
                <div className="mx-auto max-w-7xl px-4 py-14 sm:px-6 lg:px-8">
                    <div className="grid items-start gap-10 lg:grid-cols-[0.9fr_1.4fr] lg:gap-16">

                        {/* Left */}
                        <div className="lg:pt-2">
                            <div className="mb-4 text-xs font-semibold uppercase tracking-wider text-accent">
                                What professionals say
                            </div>

                            <h2 className="font-serif text-2xl font-bold leading-tight tracking-tight text-foreground sm:text-3xl">
                                Trusted by executives &amp; senior professionals
                            </h2>

                            <p className="mt-3 max-w-sm text-sm leading-6 text-muted-foreground">
                                Join thousands of professionals who are using a smarter
                                way to find their next opportunity.
                            </p>

                            <div className="mt-6 flex items-center gap-4">
                                <div>
                                    <Stars count={5} />
                                </div>

                                <div className="h-8 w-px bg-border" />

                                <div>
                                    <p className="text-sm font-bold text-foreground">
                                        4.8 / 5
                                    </p>
                                    <p className="text-xs text-muted-foreground">
                                        5,270+ reviews
                                    </p>
                                </div>
                            </div>
                        </div>

                        {/* Reviews */}
                        <div className="grid gap-6 sm:grid-cols-2">
                            {REVIEWS.slice(0, 2).map((r, i) => (
                                <MotionDiv
                                    key={r.name}
                                    custom={i}
                                    variants={fadeUp}
                                    initial="hidden"
                                    whileInView="show"
                                    viewport={viewport}
                                    className="rounded-xl border border-border bg-card p-5 shadow-sm transition-all duration-300 hover:-translate-y-0.5 hover:shadow-md"
                                >
                                    <div className="flex items-center justify-between">
                                        <Stars count={r.stars} />

                                        <span className="text-2xl font-serif leading-none text-accent/20">
                                            “
                                        </span>
                                    </div>

                                    <p className="mt-3 text-sm leading-6 text-muted-foreground">
                                        {r.text}
                                    </p>

                                    <div className="mt-5 flex items-center justify-between border-t border-border pt-4">
                                        <p className="text-sm font-semibold text-foreground">
                                            {r.name}
                                        </p>

                                        <p className="text-xs text-muted-foreground">
                                            {r.when}
                                        </p>
                                    </div>
                                </MotionDiv>
                            ))}
                        </div>
                    </div>
                </div>
            </section>



            <FaqSection />
        </div>
    );
}
